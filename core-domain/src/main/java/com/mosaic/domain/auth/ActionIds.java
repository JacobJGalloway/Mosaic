package com.mosaic.domain.auth;

/**
 * Action-ID vocabulary for the per-action-ID authorization check described
 * in ARCHITECTURE.md's Auth Design. Expected to grow as new protected
 * operations are added; not an exhaustive permission model on its own —
 * roles compose these into baseline sets, users layer overrides on top.
 */
public final class ActionIds {

    public static final String CLIENT_CREATE = "CLIENT_CREATE";
    public static final String CLIENT_READ = "CLIENT_READ";

    public static final String POLICY_CREATE = "POLICY_CREATE";
    public static final String POLICY_READ = "POLICY_READ";

    public static final String USER_MANAGE = "USER_MANAGE";

    private ActionIds() {
    }
}
