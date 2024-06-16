import "./IceRoutes.css";
import Icebreaker from "../Icebreaker/Icebreaker";
import { useEffect } from "react";

export default function IceRoutes({
  setInfoShip,
  getAllIcebreaker,
  allIcebreakers,
  getIceGantt,
  getIceRoute,
  idIcebreaker
}) {
  useEffect(() => {
    getAllIcebreaker();
  }, []);

  return (
    <div className="ice-routes">
      <ul className="ice-routes__list">
        {allIcebreakers.map((icebreaker) => {
          const info = setInfoShip(icebreaker.iceClass, icebreaker.speed);
          return (
            <Icebreaker
              key={icebreaker.id}
              icebreaker={icebreaker}
              info={info}
              getIceGantt={getIceGantt}
              getIceRoute={getIceRoute}
              idIcebreaker={idIcebreaker}
            />
          );
        })}
      </ul>
    </div>
  );
}
