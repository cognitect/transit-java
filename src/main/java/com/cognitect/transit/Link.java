// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

/**
 * Represents a hypermedia link, as per http://amundsen.com/media-types/collection/format/#arrays-links
 */
public interface Link {
    /**
     * Get the link's href
     * @return href
     */
    public URI getHref();

    /**
     * Get the link's rel
     * @return rel
     */
    public String getRel();

    /**
     * Get the link's name
     * @return name
     */
    public String getName();

    /**
     * Get the link's prompt
     * @return prompt
     */
    public String getPrompt();

    /**
     * Get the link's render semantic
     * @return render
     */
    public String getRender();
}
