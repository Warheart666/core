package org.ls.core.entities.interfaces;

import javax.persistence.Version;

public interface Versionable {

    @Version
    long version = 0;

    default long getVersion() {
        return this.version;
    }
}
