package com.xabif.arboretum

import org.cts.CRSFactory
import org.cts.crs.GeodeticCRS
import org.cts.op.CoordinateOperation
import org.cts.op.CoordinateOperationFactory
import org.cts.registry.EPSGRegistry

class CoordinateConverter {
    companion object {
        private val Factory = CRSFactory();
        private val Transform: CoordinateOperation;

        init {
            Factory.registryManager.addRegistry(EPSGRegistry());

            val epsg_25830 = Factory.getCRS("EPSG:25830") as GeodeticCRS;
            val wgs84 = Factory.getCRS("EPSG:4326") as GeodeticCRS;
            val ops = CoordinateOperationFactory.createCoordinateOperations(epsg_25830, wgs84);
            Transform = CoordinateOperationFactory.getMostPrecise(ops);
        }

        fun convert(coords: DoubleArray) : DoubleArray {
            return Transform.transform(coords);
        }
    }
}
