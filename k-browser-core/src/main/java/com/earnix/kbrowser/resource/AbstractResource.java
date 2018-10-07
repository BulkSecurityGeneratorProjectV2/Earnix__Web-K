/*
 * {{{ header & license
 * Copyright (c) 2004, 2005 Who?
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * }}}
 */
package com.earnix.kbrowser.resource;

import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * @author Patrick Wright
 */
public abstract class AbstractResource implements Resource {
    private InputSource inputSource;
    private long createTimeStamp;
    private long elapsedLoadTime;

    private AbstractResource() {
        this.createTimeStamp = System.currentTimeMillis();
    }

    /**
     * Creates a new instance of AbstractResource
     */
    public AbstractResource(InputSource source) {
        this();
        this.inputSource = source;
    }

    public AbstractResource(InputStream is) {
        this(is == null ? (InputSource) null : new InputSource(new BufferedInputStream(is)));
    }

    public InputSource getResourceInputSource() {
        return this.inputSource;
    }

    public long getResourceLoadTimeStamp() {
        return this.createTimeStamp;
    }

    public long getElapsedLoadTime() {
        return elapsedLoadTime;
    }

    /*package*/
    void setElapsedLoadTime(long elapsedLoadTime) {
        this.elapsedLoadTime = elapsedLoadTime;
    }
}