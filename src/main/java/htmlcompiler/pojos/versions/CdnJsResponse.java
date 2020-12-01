package htmlcompiler.pojos.versions;

import java.util.List;

public final class CdnJsResponse {
    public final List<String> versions;
    public CdnJsResponse(final List<String> versions) {
        this.versions = versions;
    }
}
