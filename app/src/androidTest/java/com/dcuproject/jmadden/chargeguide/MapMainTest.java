package com.dcuproject.jmadden.chargeguide;

import org.hamcrest.Matcher;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by jmadden on 09/03/18.
 */
public class MapMainTest {
    @Test
    public void getDistanceToDestinationTest() throws Exception {
        // 7.466440000000034 53.62549
        // 95.7778253173828
        assertThat(MapMain.getDistanceToDestination(53.62549, -7.466440000000034, 53.366299, -6.31205), equalTo(95.7778253173828));
    }

    @Test
    public void buildMapUrlTest() throws Exception {

    }

}