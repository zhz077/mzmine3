/* 
 * (C) Copyright 2015 by MSDK Development Team
 *
 * This software is dual-licensed under either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */

package io.github.msdk.io.rawdataimport.mzml;

/**
 * Controlled vocabulary (CV) values for mzML files.
 */
class JMzMLCV {

    // Scan start time
    static final String cvScanStartTime = "MS:1000016";

    // Minutes unit. MS:1000038 is used in mzML 1.0, while UO:000003 is used in
    // mzML 1.1.0
    static final String cvUnitsMin1 = "MS:1000038";
    static final String cvUnitsMin2 = "UO:0000031";

    // Polarity
    static final String cvPolarityPositive = "MS:1000130";
    static final String cvPolarityNegative = "MS:1000129";

    // Chromatograms
    static final String cvChromatogramTIC = "MS:1000235";
    static final String cvChromatogramMRM_SRM = "MS:1001473";
    static final String cvChromatogramSIC = "MS:1000627";
    static final String cvChromatogramBPC = "MS:1000628";

    // Activation
    static final String cvActivationEnergy = "MS:1000045";
    static final String cvActivationCID = "MS:1000133";

    // Isolation
    static final String cvIsolationWindow = "MS:1000827";
}
