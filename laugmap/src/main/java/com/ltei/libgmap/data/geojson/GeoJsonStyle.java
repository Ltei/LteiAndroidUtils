package com.ltei.laugmap.data.geojson;

/**
 * Class used to apply styles for the
 * {@link com.ltei.laugmap.data.geojson.GeoJsonFeature GeoJsonFeature} objects
 */
interface GeoJsonStyle {

    /**
     * Gets the type of geometries this style can be applied to
     *
     * @return type of geometries this style can be applied to
     */
    String[] getGeometryType();

    boolean isVisible();

    void setVisible(boolean visible);

}
