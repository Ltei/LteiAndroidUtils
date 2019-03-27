/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltei.laugmap.clustering.algo;

import com.google.android.gms.maps.GoogleMap;
import com.ltei.laugmap.clustering.ClusterItem;

/**
 *
 * This algorithm uses map position for clustering, and should be reclustered on map movement
 * @param <T>
 */

public interface ScreenBasedAlgorithm<T extends ClusterItem> extends Algorithm<T>, GoogleMap.OnCameraChangeListener {

    boolean shouldReclusterOnMapMovement();
}
