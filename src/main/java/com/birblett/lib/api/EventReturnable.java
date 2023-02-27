package com.birblett.lib.api;

/**
 * Returnable values for events that tell the associated Injector whether it should cancel or not after or during event
 * hook processing
 */
public enum EventReturnable {
    /** Nothing is done after event execution */
    NO_OP,
    /** Cancel after all callbacks in the current event are finished executing*/
    RETURN_AFTER_FINISH,
    /** Cancel immediately after executing the current event callback */
    RETURN_IMMEDIATELY
}
