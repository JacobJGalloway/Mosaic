package com.mosaic.api.auth;

import java.util.Set;

public record UserOverridesRequest(Set<String> actionOverridesAdd, Set<String> actionOverridesRemove) {
}
