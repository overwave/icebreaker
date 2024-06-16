import "./AdminApplications.css";
import { useState } from "react";
import { adminApplications } from "../../configs/constants";
import ApplicationsConstructor from "../ApplicationsConstructor/ApplicationsConstructor";
import IceRoutes from "../IceRoutes/IceRoutes";

export default function AdminApplications({
  applicationsPoints,
  allApplications,
  setInfoShip,
  getShipRoute,
  getAllIcebreaker,
  allIcebreakers,
  setNewRouteList,
  getGantt,
  getIceGantt,
  getIceRoute,
  idIcebreaker
}) {
  const [selectedApplications, setSelectedApplications] = useState(
    Number(localStorage.getItem("selectedApplications"))
      ? Number(localStorage.getItem("selectedApplications"))
      : 0
  );
  const [selectedMenu, setSelectedMenu] = useState(
    localStorage.getItem("selectedMenu")
      ? localStorage.getItem("selectedMenu")
      : "applications"
  );

  function choiceApplications() {
    setSelectedMenu("applications");
    localStorage.setItem("selectedMenu", "applications");
  }

  function choiceIceRoutes() {
    setSelectedMenu("routes");
    localStorage.setItem("selectedMenu", "routes");
  }

  return (
    <div className="admin-applications">
      <div className="admin-applications__content">
        <div className="admin-applications__header">
          <ul className="admin-applications__header-list">
            <li
              className={`admin-applications__header-item ${
                selectedMenu === "applications"
                  ? "admin-applications__header-item_active"
                  : ""
              }`}
              onClick={choiceApplications}
            >
              Заявки
            </li>
            <li
              className={`admin-applications__header-item ${
                selectedMenu === "routes"
                  ? "admin-applications__header-item_active"
                  : ""
              }`}
              onClick={choiceIceRoutes}
            >
              Маршруты ледоколов
            </li>
          </ul>
        </div>
        <div
          className={`admin-applications__body ${
            selectedMenu === "routes"
              ? "admin-applications__body_name_routes"
              : ""
          }`}
        >
          {selectedMenu === "applications" && (
            <>
              <nav className="admin-applications__nav">
                <ul className="admin-applications__nav-list">
                  {adminApplications.map((item, index) => {
                    function onClick() {
                      localStorage.setItem("selectedApplications", index);
                      setSelectedApplications(index);
                    }
                    return (
                      <li
                        key={index}
                        className={`admin-applications__nav-item ${
                          selectedApplications === index
                            ? "admin-applications__nav-item_active"
                            : ""
                        }`}
                        onClick={onClick}
                      >
                        <span className="admin-applications__nav-text">
                          {item.name}
                        </span>
                        <span className="admin-applications__nav-number">
                          {applicationsPoints[item.type]}
                        </span>
                      </li>
                    );
                  })}
                </ul>
              </nav>
              <div className="admin-applications__block">
                <ul className="admin-applications__list">
                  {selectedApplications === 0 && (
                    <ApplicationsConstructor
                      applications={allApplications.pending}
                      text="Новых заявок нет"
                      status="pending"
                      myClass=""
                      setInfoShip={setInfoShip}
                    />
                  )}

                  {selectedApplications === 1 && (
                    <ApplicationsConstructor
                      applications={allApplications.agreed}
                      text="Согласованных заявок нет"
                      status="agreed"
                      myClass="agreed"
                      getShipRoute={getShipRoute}
                      setInfoShip={setInfoShip}
                    />
                  )}

                  {selectedApplications === 2 && (
                    <ApplicationsConstructor
                      applications={allApplications.archive}
                      text="Здесь пока ничего нет"
                      status="archive"
                      myClass=""
                      setInfoShip={setInfoShip}
                    />
                  )}
                </ul>
              </div>
            </>
          )}

          {selectedMenu === "routes" && (
            <IceRoutes
              setInfoShip={setInfoShip}
              getAllIcebreaker={getAllIcebreaker}
              allIcebreakers={allIcebreakers}
              getIceGantt={getIceGantt}
              getIceRoute={getIceRoute}
              idIcebreaker={idIcebreaker}
            />
          )}
        </div>

        {selectedApplications === 0 && selectedMenu === "applications" && (
          <div className="admin-applications__footer">
            <button
              className="admin-applications__btn content__btn"
              type="button"
              onClick={setNewRouteList}
            >
              Сформировать маршруты
            </button>
          </div>
        )}

        {allApplications.agreed &&
          selectedApplications === 1 &&
          selectedMenu === "applications" &&
          allApplications.agreed.length !== 0 && (
            <div className="admin-applications__footer">
              <button
                className="applications__btn_secondary content__btn_secondary"
                type="button"
                onClick={getGantt}
              >
                Скачать диаграмму Ганта
              </button>
            </div>
          )}
      </div>
    </div>
  );
}
