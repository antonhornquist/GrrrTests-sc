GRTestsHelper {
	classvar
		savedIndicateAddedRemovedAttachedDetachedFlag,
		savedTraceButtonEvents,
		savedTraceLedEvents
	;

	*saveGlobals {
		savedIndicateAddedRemovedAttachedDetachedFlag = GRCommon.indicateAddedRemovedAttachedDetached;
		savedTraceButtonEvents = GRCommon.traceButtonEvents;
		savedTraceLedEvents = GRCommon.traceLedEvents;
	}

	*disableTraceAndFlash {
		GRCommon.indicateAddedRemovedAttachedDetached = false;
		GRCommon.traceButtonEvents = false;
		GRCommon.traceLedEvents = false;
	}

	*restoreGlobals {
		GRCommon.indicateAddedRemovedAttachedDetached = savedIndicateAddedRemovedAttachedDetachedFlag;
		GRCommon.traceButtonEvents = savedTraceButtonEvents;
		GRCommon.traceLedEvents = savedTraceLedEvents;
	}
}
