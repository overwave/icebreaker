import { useEffect, useState } from "react";
import Maps from "../Maps/Maps";
import "./Main.css";
import Applications from "../Applications/Applications";

export default function Main({ getNavigationPoints, navPoints, getShips }) {

    const [shipGeo, setShipGeo] = useState(0);

    useEffect(() => {
        getNavigationPoints();
    }, []);

    function change(e) {
        setShipGeo(Number(e.target.value));
    }

    return (
        <section className="main">
            <Maps navPoints={navPoints} shipGeo={shipGeo} />
            <div className="main__calendar">
                <input className="main__input" type="range" min="0" max="3" value={shipGeo} onChange={change} />
            </div>
            <Applications getShips={getShips} />
        </section>
    );
}