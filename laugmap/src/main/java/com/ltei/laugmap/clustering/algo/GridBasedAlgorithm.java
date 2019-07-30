package com.ltei.laugmap.clustering.algo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.util.LongSparseArray;
import com.ltei.laugmap.clustering.Cluster;
import com.ltei.laugmap.clustering.ClusterItem;
import com.ltei.laugmap.geometry.Point;
import com.ltei.laugmap.projection.SphericalMercatorProjection;

/**
 * Groups markers into a grid.
 */
public class GridBasedAlgorithm<T extends ClusterItem> implements Algorithm<T> {
    private static final int DEFAULT_GRID_SIZE = 100;

    private int mGridSize = DEFAULT_GRID_SIZE;

    private final Set<T> mItems = Collections.synchronizedSet(new HashSet<T>());

    @Override
    public void addItem(T item) {
        mItems.add(item);
    }

    @Override
    public void addItems(Collection<T> items) {
        mItems.addAll(items);
    }

    @Override
    public void clearItems() {
        mItems.clear();
    }

    @Override
    public void removeItem(T item) {
        mItems.remove(item);
    }

    @Override
    public void setMaxDistanceBetweenClusteredItems(int maxDistance) {
        mGridSize = maxDistance;
    }

    @Override
    public int getMaxDistanceBetweenClusteredItems() {
        return mGridSize;
    }

    @Override
    public Set<? extends Cluster<T>> getClusters(double zoom) {
        long numCells = (long) Math.ceil(256 * Math.pow(2, zoom) / mGridSize);
        SphericalMercatorProjection proj = new SphericalMercatorProjection(numCells);

        HashSet<Cluster<T>> clusters = new HashSet<>();
        LongSparseArray<StaticCluster<T>> sparseArray = new LongSparseArray<>();

        synchronized (mItems) {
            for (T item : mItems) {
                Point p = proj.toPoint(item.getPosition());

                long coord = getCoord(numCells, p.x, p.y);

                StaticCluster<T> cluster = sparseArray.get(coord);
                if (cluster == null) {
                    cluster = new StaticCluster<>(proj.toLatLng(new Point(Math.floor(p.x) + .5, Math.floor(p.y) + .5)));
                    sparseArray.put(coord, cluster);
                    clusters.add(cluster);
                }
                cluster.add(item);
            }
        }

        return clusters;
    }

    @Override
    public Collection<T> getItems() {
        return mItems;
    }

    private static long getCoord(long numCells, double x, double y) {
        return (long) (numCells * Math.floor(x) + Math.floor(y));
    }
}