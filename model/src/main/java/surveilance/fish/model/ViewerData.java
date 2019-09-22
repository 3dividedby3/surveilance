package surveilance.fish.model;

import java.util.List;
import java.util.Map;

public final class ViewerData {

    private Long timestamp;

    private Map<String, List<String>> headers;
    
    private String body;
    
    public ViewerData() {
        //made for jackson
    }
    
    public ViewerData(Map<String, List<String>> headers, String body) {
        timestamp = System.currentTimeMillis();
        this.headers = headers;
        this.body = body;
    }

    public Long getTimestamp() {
        return timestamp;
    }
    
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        //TODO: replace with java.util.Objects.hash(body, headers, timestamp);
        final int prime = 31;
        int result = 1;
        result = prime * result + ((body == null) ? 0 : body.hashCode());
        result = prime * result + ((headers == null) ? 0 : headers.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ViewerData)) {
            return false;
        }
        ViewerData other = (ViewerData) obj;
        if (body == null) {
            if (other.body != null) {
                return false;
            }
        } else if (!body.equals(other.body)) {
            return false;
        }
        if (headers == null) {
            if (other.headers != null) {
                return false;
            }
        } else if (!headers.equals(other.headers)) {
            return false;
        }
        if (timestamp == null) {
            if (other.timestamp != null) {
                return false;
            }
        } else if (!timestamp.equals(other.timestamp)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "ViewerData [timestamp=" + timestamp + ", headers=" + headers + ", body=" + body + "]";
    }
}
