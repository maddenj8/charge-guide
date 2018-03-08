package com.dcuproject.jmadden.chargeguide;

import android.util.Log;

import com.dcuproject.jmadden.chargeguide.MapMain;
/**
 * Created by jmadden on 07/03/18.
 */

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.regex.Pattern;
import static org.junit.Assert.*;


public class MapMainUnitTest {
    @Test
    public void checkDistanceCorrect() {
        double distance = MapMain.getDistanceToDestination((float) 53.62549, (float) -7.466440000000034, (float) 53.349804, (float) -6.2603097);
        assertEquals("distance is wrong", distance, (float) 100.31303031921387);
    }


}
