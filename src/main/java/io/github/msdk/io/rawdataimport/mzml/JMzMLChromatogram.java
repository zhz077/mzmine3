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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import io.github.msdk.MSDKRuntimeException;
import io.github.msdk.datamodel.chromatograms.Chromatogram;
import io.github.msdk.datamodel.chromatograms.ChromatogramDataPointList;
import io.github.msdk.datamodel.chromatograms.ChromatogramType;
import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.msspectra.MsSpectrumDataPointList;
import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.msdk.datamodel.rawdata.IsolationInfo;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.datamodel.rawdata.SeparationType;
import uk.ac.ebi.jmzml.model.mzml.Spectrum;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

class JMzMLChromatogram implements Chromatogram {

    private final @Nonnull JMzMLRawDataFile dataFile;
    private final @Nonnull String chromatogramId;
    private final @Nonnull Integer chromatogramNumber;
    private final @Nonnull ChromatogramType chromatogramType;
    private final @Nonnull SeparationType separationType;
    private final @Nonnull List<IsolationInfo> isolations;

    JMzMLChromatogram(@Nonnull JMzMLRawDataFile dataFile,
            @Nonnull String chromatogramId,
            @Nonnull Integer chromatogramNumber,
            @Nonnull SeparationType separationType,
            @Nonnull ChromatogramType chromatogramType,
            @Nonnull List<IsolationInfo> isolations) {
        this.dataFile = dataFile;
        this.chromatogramId = chromatogramId;
        this.chromatogramNumber = chromatogramNumber;
        this.separationType = separationType;
        this.chromatogramType = chromatogramType;
        this.isolations = isolations;
    }

    @Override
    @Nullable
    public RawDataFile getRawDataFile() {
        return dataFile;
    }

    @Override
    @Nonnull
    public Integer getChromatogramNumber() {
        return chromatogramNumber;
    }

    @Override
    @Nonnull
    public ChromatogramType getChromatogramType() {
        return chromatogramType;
    }

    @Override
    public void getDataPoints(@Nonnull ChromatogramDataPointList dataPointList) {
        try {
            MzMLUnmarshaller parser = dataFile.getParser();
            if (parser == null) {
                throw new MSDKRuntimeException(
                        "The raw data file object has been disposed");
            }
            uk.ac.ebi.jmzml.model.mzml.Chromatogram jmzChromatogram = parser.getChromatogramById(chromatogramId);
            JMzMLUtil.extractDataPoints(jmzChromatogram, dataPointList);
        } catch (MzMLUnmarshallerException e) {
            throw (new MSDKRuntimeException(e));
        }
    }

    @Override
    @Nonnull
    public List<IsolationInfo> getIsolations() {
        return isolations;
    }

    @Override
    @Nonnull
    public SeparationType getSeparationType() {
        return separationType;
    }

    @Override
    @Nonnull
    public Chromatogram clone(@Nonnull DataPointStore newStore) {
        Preconditions.checkNotNull(newStore);
        Chromatogram newChromatogram = MSDKObjectBuilder.getChromatogram(
                newStore, getChromatogramNumber(), getChromatogramType(),
                getSeparationType());

        final ChromatogramDataPointList dataPointList = MSDKObjectBuilder
                .getChromatogramDataPointList();
        getDataPoints(dataPointList);

        final RawDataFile rawDataFile2 = getRawDataFile();
        if (rawDataFile2 != null) {
            newChromatogram.setRawDataFile(rawDataFile2);
        }
        newChromatogram.getIsolations().addAll(getIsolations());
        newChromatogram.setDataPoints(dataPointList);
        return newChromatogram;
    }

    /*
     * Unsupported set-operations
     */

    @Override
    public void setRawDataFile(@Nonnull RawDataFile newRawDataFile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChromatogramNumber(@Nonnull Integer chromatogramNumber) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChromatogramType(
            @Nonnull ChromatogramType newChromatogramType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDataPoints(
            @Nonnull ChromatogramDataPointList newDataPoints) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSeparationType(@Nonnull SeparationType separationType) {
        throw new UnsupportedOperationException();
    }

}
