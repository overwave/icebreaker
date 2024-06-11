

import "./Maps.css";
import React, { useCallback, useEffect, useRef, useState } from "react";
import { YMaps, Map, Polyline, Placemark, ObjectManager, Clusterer } from "@pbe/react-yandex-maps";
import icon from "../../images/icon.png";
import pointIcon from "../../images/point.svg";

export default function Maps({ navPoints, shipGeo }) {
    const [geometryPoints, setGeometryPoints] = useState([]);

    const ymap = useRef(null);
    const ymaps = useRef(null);
    const geometry = [[70.28319, 57.787407], [71.284454, 60.68428], [74.284454, 62.68428], [75.284454, 66.68428]];

    const onLoad = useCallback(it => {
            ymaps.current = it;
            const layer = new ymaps.current.Layer('https://overwave.dev/icebreaker/tiles/2020-03-03/%z/%x-%y.jpg', {tileTransparent: true});
            ymap.current.layers.add(layer);
        }, [ymap, ymaps]
    );
    const onMapReference = useCallback(ref => {
        ymap.current = ref;
    }, [ymap]);

    useEffect(() => {
        if (navPoints.length) {
            let arr = [];
            navPoints.map((point) => {
                arr.push({
                    point: [point.point.lat, point.point.lon],
                    name: point.name
                });
            });
            setGeometryPoints(arr);
        }
    }, [navPoints]);

    return (
        <>
            <YMaps query={{load: 'Map,Layer,geoObject.addon.balloon', lang: "ru_RU", ns: "use-load-option"}}>
                <div>
                    <Map defaultState={{center: [73.290841, 55.573121], zoom: 5}}
                         options={{yandexMapDisablePoiInteractivity: true, minZoom: 3, maxZoom: 8}}
                         onLoad={onLoad}
                         instanceRef={onMapReference}
                         style={{width: '100vw', height: 'calc(100vh - 140px)'}}
                    >
                        {/* <Polyline geometry={geometry} options={{
                            balloonCloseButton: false,
                            strokeColor: '#f85858',
                            strokeWidth: 2,
                            strokeOpacity: 0.5
                        }}></Polyline> */}

                        <Clusterer options={{
                            preset: "islands#invertedVioletClusterIcons",
                            groupByCoordinates: false
                            }}>
                                {geometryPoints.map((point, index) => {
                                    return <Placemark key={index} defaultGeometry={point.point} options={{
                                        iconLayout: "default#image",
                                        iconImageSize: [17, 17],
                                        iconImageHref: pointIcon,
                                        iconOffset: [0, 0]
                                      }} properties={{
                                        balloonContentBody:
                                            point.name
                                      }} />
                                })}
                        </Clusterer>

                        <Placemark geometry={geometry[shipGeo]} options={{
                            iconLayout: "default#image",
                            iconImageSize: [50, 50],
                            iconImageHref: icon,
                            iconOffset: [-15, 12]
                          }} />
                    </Map>
                </div>
            </YMaps>
        </>
    );
}