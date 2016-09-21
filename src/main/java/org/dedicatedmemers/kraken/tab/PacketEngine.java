package org.dedicatedmemers.kraken.tab;

/**
 * @author DarkSeraphim.
 */
public interface PacketEngine {

    // Enable batch processing
    void setBatch(boolean flag);

    void update(TabEntry entry);
}
