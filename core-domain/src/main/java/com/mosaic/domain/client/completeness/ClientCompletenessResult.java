package com.mosaic.domain.client.completeness;

import java.util.List;

public record ClientCompletenessResult(
        boolean complete,
        List<CompletenessRequirement> missingRequirements
) {
}
