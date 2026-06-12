package com.acme.coldtrace.platform.identityaccess.interfaces.acl;

/**
 * Published anti-corruption facade for the identity access bounded context.
 * <p>
 * Other bounded contexts use this interface instead of depending on identity
 * repositories or aggregates directly. The facade exposes only the small
 * organization-related language needed by current Sprint 3 use cases.
 *
 * @since 1.0
 */
public interface IdentityAccessContextFacade {
    /**
     * Checks whether an organization exists.
     *
     * @param organizationId organization identifier
     * @return {@code true} when the organization exists
     */
    boolean organizationExists(Long organizationId);
}
