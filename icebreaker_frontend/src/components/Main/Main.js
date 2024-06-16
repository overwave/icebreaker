import { useContext, useEffect, useState } from "react";
import Maps from "../Maps/Maps";
import "./Main.css";
import Applications from "../Applications/Applications";
import { CurrentUserContext } from "../../contexts/CurrentUserContext";
import MyCalendar from "../MyCalendar/MyCalendar";
import AdminApplications from "../AdminApplications/AdminApplications";

export default function Main({
  getNavigationPoints,
  navPoints,
  getAllApplications,
  applicationsPoints,
  allApplications,
  setIsPopupNewApplication,
  addZero,
  setInfoShip,
  shipRoute,
  getShipRoute,
  getAllIcebreaker,
  allIcebreakers,
  setNewRouteList,
  getGantt,
  getIceGantt,
  getIceRoute,
  iceRoute,
  idIcebreaker,
  dateIceFlotation
}) {
  const [shipGeo, setShipGeo] = useState(0);
  const currentUser = useContext(CurrentUserContext);

  useEffect(() => {
    getAllApplications();
    getNavigationPoints();
  }, []);

  return (
    <section
      className={`main ${
        currentUser.currentUser.role === "ADMIN" ? "main_admin" : ""
      }`}
    >
      {currentUser.currentUser.role === "CAPTAIN" && (
        <Applications
          applicationsPoints={applicationsPoints}
          allApplications={allApplications}
          setIsPopupNewApplication={setIsPopupNewApplication}
          getShipRoute={getShipRoute}
          setInfoShip={setInfoShip}
          getGantt={getGantt}
        />
      )}

      {currentUser.currentUser.role === "ADMIN" && (
        <AdminApplications
          applicationsPoints={applicationsPoints}
          allApplications={allApplications}
          setInfoShip={setInfoShip}
          getShipRoute={getShipRoute}
          getAllIcebreaker={getAllIcebreaker}
          allIcebreakers={allIcebreakers}
          setNewRouteList={setNewRouteList}
          getGantt={getGantt}
          getIceGantt={getIceGantt}
          getIceRoute={getIceRoute}
          idIcebreaker={idIcebreaker}
        />
      )}

      <div className="main__maps">
        <Maps
          navPoints={navPoints}
          shipGeo={shipGeo}
          shipRoute={shipRoute}
          iceRoute={iceRoute}
          idIcebreaker={idIcebreaker}
          dateIceFlotation={dateIceFlotation}
        />
      </div>

      <div className="main__time">
        <MyCalendar
          addZero={addZero}
          shipRoute={shipRoute}
          iceRoute={iceRoute}
          shipGeo={shipGeo}
          setShipGeo={setShipGeo}
        />
      </div>
    </section>
  );
}
