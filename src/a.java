/*
 * ExecSetOp for hashed case: phase 1, read input and build hash table
 */
static void
setop_fill_hash_table(SetOpState *setopstate)
{
	SetOp	   *node = (SetOp *) setopstate->ps.plan;
	PlanState  *outerPlan;
	int			firstFlag;
	bool		in_first_rel;
         
	/*
	 * get state info from node
	 */
	outerPlan = outerPlanState(setopstate);
	firstFlag = node->firstFlag;
	/* verify planner didn't mess up */
	Assert(firstFlag == 0 ||
		   (firstFlag == 1 &&
			(node->cmd == SETOPCMD_INTERSECT ||
			 node->cmd == SETOPCMD_INTERSECT_ALL ||
			 node->cmd == SETOPCMD_COMBINE  )));

	/*
	 * Process each outer-plan tuple, and then fetch the next one, until we
	 * exhaust the outer plan.
	 */
	in_first_rel = true;
	for (;;)
	{
		TupleTableSlot *outerslot;
		TupleTableSlot *nslot;
		int			flag;
		SetOpHashEntry entry;
		bool		isnew;
		Datum *replValues;
		bool *replIsnull;
		bool *doReplace;
                int natts;
		int i;

		outerslot = ExecProcNode(outerPlan);
		if (TupIsNull(outerslot))
			break;

		/* Identify whether it's left or right input */
		flag = fetch_tuple_flag(setopstate, outerslot);

		if (flag == firstFlag)
		{
			/* (still) in first input relation */
			Assert(in_first_rel);

			/* Find or build hashtable entry for this tuple's group */
			entry = (SetOpHashEntry)
				LookupTupleHashEntry(setopstate->hashtable, outerslot, &isnew);

			/* If new tuple group, initialize counts */
			if (isnew)
				initialize_counts(&entry->pergroup);

			/* Advance the counts */
			advance_counts(&entry->pergroup, flag);
		}
		else
		{
			/* reached second relation */
			in_first_rel = false;
			if (node->cmd == SETOPCMD_COMBINE) {
				natts=outerslot->tts_tupleDescriptor->natts;

				slot_getallattrs(outerslot); 
                 
				/* Find or build hashtable entry for this tuple's group */
				entry = (SetOpHashEntry)
					LookupTupleHashEntry(setopstate->hashtable,
							     outerslot,
							     NULL);

				/* Copy values from entry, if exists*/
				if (entry) {

					replValues = (Datum*) palloc(sizeof(Datum)*natts);
					replIsnull = (bool*) palloc(sizeof(bool)*natts);
					doReplace = (bool*) palloc(sizeof(bool)*natts);

					for(i=0;i<natts;i++) {
						replValues[i] = outerslot->tts_values[i];
						replIsnull[i] = outerslot->tts_isnull[i];
						doReplace[i] = 1;
					}
			
					RemoveTupleHashEntry(setopstate->hashtable, outerslot);
					ExecStoreMinimalTuple(
							entry->shared.firstTuple,outerslot,false);

					slot_getallattrs(outerslot);
					for(i=1;i<natts;i++) {
						replValues[i] += outerslot->tts_values[i];
					}

					nslot=MakeSingleTupleTableSlot(
								outerslot->tts_tupleDescriptor);

					nslot=ExecStoreTuple(heap_modify_tuple(nslot,
							  outerslot->tts_tupleDescriptor,
							  replValues,
							  replIsnull,
							  doReplace), 
							nslot, InvalidBuffer, false);

					slot_getallattrs(nslot); 
					LookupTupleHashEntry(setopstate->hashtable,
							     nslot,
							     &isnew);
					
					free(replValues);
					free(replIsnull);
					free(doReplace);				
  
				} else {
					LookupTupleHashEntry(setopstate->hashtable, outerslot, &isnew);
				}				
				 
			} else {
				/* For tuples not seen previously, do not make hashtable entry */
				entry = (SetOpHashEntry)
					LookupTupleHashEntry(setopstate->hashtable, outerslot, NULL);
				/* Advance the counts if entry is already present */
				if (entry)
					advance_counts(&entry->pergroup, flag);
			}
		}

		/* Must reset temp context after each hashtable lookup */
		MemoryContextReset(setopstate->tempContext);
	}

	setopstate->table_filled = true;
	/* Initialize to walk the hash table */
	ResetTupleHashIterator(setopstate->hashtable, &setopstate->hashiter);
}
