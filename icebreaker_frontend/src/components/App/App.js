import React, { useState, useEffect } from "react";
import "./App.css";
import { Switch, Route, useHistory, useLocation } from "react-router-dom";
import Login from "../Login/Login";
import Register from "../Register/Register";
import PageNotFound from "../PageNotFound/PageNotFound";
import Preloader from "../Preloader/Preloader";
import mainApi from "../../utils/MainApi";
import { CurrentUserContext } from "../../contexts/CurrentUserContext";
import {
  errors,
  errorsRegister,
  errorsLogin,
  errorsUpdate,
} from "../../configs/errors";
import Main from "../Main/Main";
import Header from "../Header/Header";
import ProtectedRoute from "../ProtectedRoute/ProtectedRoute";
import PopupNewApplication from "../PopupNewApplication/PopupNewApplication";
import { nodeCases } from "../../configs/constants";
import PopupNewShip from "../PopupNewShip/PopupNewShip";
import PreloaderRoutes from "../PreloaderRoutes/PreloaderRoutes";

function App() {
  // Ошибки
  const [isError, setIsError] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  // Загрузка
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingRoutes, setIsLoadingRoutes] = useState(false);

  // Пользователь
  const [loggedIn, setLoggedIn] = useState(
    localStorage.getItem("loggedIn") ? localStorage.getItem("loggedIn") : false
  );
  const [currentUser, setCurrentUser] = useState({
    role: "",
    login: "",
  });

  // Опорные точки
  const [navPoints, setNavPoints] = useState(
    localStorage.getItem("navPoints")
      ? JSON.parse(localStorage.getItem("navPoints"))
      : []
  );

  // Количество заявок Пользователя
  const [applicationsPoints, setApplicationsPoints] = useState({
    agreed: 0,
    pending: 0,
    archive: 0,
  });
  const [allApplications, setAllApplications] = useState([]);

  // Попапы
  const [isPopupNewApplication, setIsPopupNewApplication] = useState(false);
  const [isPopupNewShip, setIsPopupNewShip] = useState(false);

  // Список кораблей
  const [ships, setShips] = useState([]);

  // Ледовые классы
  const [iceClasses, setIceClasses] = useState([]);

  // Маршрут корабля
  const [shipRoute, setShipRoute] = useState(
    localStorage.getItem("shipRoute")
      ? JSON.parse(localStorage.getItem("shipRoute"))
      : []
  );

  // Маршрут ледокола
  const [iceRoute, setIceRoute] = useState(
    localStorage.getItem("iceRoute")
      ? JSON.parse(localStorage.getItem("iceRoute"))
      : []
  );
  const [idIcebreaker, setIdIcebreaker] = useState(
    localStorage.getItem("idIcebreaker")
      ? JSON.parse(localStorage.getItem("idIcebreaker"))
      : undefined
  );

  // Все ледоколы
  const [allIcebreakers, setAllIcebreakers] = useState([]);

  const history = useHistory();
  const { pathname } = useLocation();

  useEffect(() => {
    if (
      loggedIn &&
      (pathname === "/icebreaker/signin" || pathname === "/icebreaker/signup")
    ) {
      history.push("/icebreaker/");
    }
    setIsError(false);
  }, [loggedIn, history, pathname]);

  useEffect(() => {
    const isLoggedIn = localStorage.getItem("loggedIn");
    if (isLoggedIn) {
      getUserInfo();
    }

    //handleLogout();
  }, [loggedIn]);

  function createError(errorsList, err) {
    if (errorsList[err] !== undefined) {
      setErrorMessage(errorsList[err]);
    } else {
      setErrorMessage(errors[500]);
    }
    setIsError(true);
  }

  function handleLogin({ login, password }) {
    setIsLoading(true);
    mainApi
      .authorize({ login, password })
      .then((res) => {
        if (res.result === "SUCCESS") {
          localStorage.setItem("loggedIn", true);
          setLoggedIn(true);
          window.location.reload();
        }
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
        createError(errorsLogin, err.status);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  function handleRegister({ role, login, password }) {
    setIsLoading(true);
    mainApi
      .register({ role, login, password })
      .then((res) => {
        if (res === "SUCCESS") {
          handleLogin({ login, password });
        }
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
        createError(errorsRegister, err.status);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  function handleLogout() {
    setIsLoading(true);
    mainApi
      .logout()
      .then((res) => {
        localStorage.clear();
        setCurrentUser({
          role: "",
          login: "",
        });
        setLoggedIn(false);
        window.location.reload();
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  // Информация о пользователе
  function getUserInfo() {
    setIsLoading(true);
    mainApi
      .getUserInfo()
      .then((res) => {
        setLoggedIn(true);
        setCurrentUser(res);
      })
      .catch((err) => {
        handleLogout();
        console.log(`Ошибка: ${err}`);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  // Опорные точки
  function getNavigationPoints() {
    setIsLoading(true);
    mainApi
      .getNavigationPoints()
      .then((res) => {
        setNavPoints(res);
        localStorage.setItem("navPoints", JSON.stringify(res));
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  // Заявки
  function getAllApplications() {
    setIsLoading(true);
    mainApi
      .getAllApplications()
      .then((res) => {
        setAllApplications(res);

        const points = {
          agreed: res.agreed.length,
          pending: res.pending.length,
          archive: res.archive.length,
        };
        setApplicationsPoints(points);
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  // Подача новой заявки
  function setNewApplication({
    shipId,
    startPointId,
    finishPointId,
    startDate,
  }) {
    setIsLoading(true);
    mainApi
      .setNewApplication({ shipId, startPointId, finishPointId, startDate })
      .then((res) => {
        const newApplications = {
          agreed: allApplications.agreed,
          pending: res.pending,
          archive: allApplications.archive,
        };
        setAllApplications(newApplications);

        const points = {
          agreed: newApplications.agreed.length,
          pending: newApplications.pending.length,
          archive: newApplications.archive.length,
        };
        setApplicationsPoints(points);

        closeAllPopups();
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  // Все корабли
  function getShips() {
    setIsLoading(true);
    mainApi
      .getShips()
      .then((res) => {
        setShips(res.ships);
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  // Создание нового корабля
  function setNewShip({ name, speed, iceClass }) {
    setIsLoading(true);
    mainApi
      .setNewShip({ name, speed, iceClass })
      .then((res) => {
        setShips(res.ships);

        setIsPopupNewShip(false);
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  // Ледовые классы
  function getIceClasses() {
    setIsLoading(true);
    mainApi
      .getIceClasses()
      .then((res) => {
        setIceClasses(res.iceClasses);
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  // Маршрут корабля
  function getShipRoute({ id }) {
    setIsLoading(true);
    mainApi
      .getShipRoute({ id })
      .then((res) => {
        setIceRoute([]);
        localStorage.removeItem("iceRoute");
        setIdIcebreaker(undefined);
        localStorage.removeItem("idIcebreaker");

        setShipRoute(res);
        localStorage.setItem("shipRoute", JSON.stringify(res));

        window.location.reload();
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  // Маршрут ледокола
  function getIceRoute(id) {
    setIsLoading(true);
    mainApi
      .getIceRoute(id)
      .then((res) => {
        setShipRoute([]);
        localStorage.removeItem("shipRoute");
        localStorage.removeItem("activeApplication");

        setIdIcebreaker(id);
        localStorage.setItem("idIcebreaker", id);

        setIceRoute(res.segments);
        localStorage.setItem("iceRoute", JSON.stringify(res.segments));

        window.location.reload();
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  // Все ледоколы
  function getAllIcebreaker() {
    setIsLoading(true);
    mainApi
      .getAllIcebreaker()
      .then((res) => {
        setAllIcebreakers(res.icebreakers);
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  // Формирование нового маршрута
  function setNewRouteList() {
    setIsLoadingRoutes(true);
    mainApi
      .setNewRouteList()
      .then((res) => {
        setAllApplications(res);

        const points = {
          agreed: res.agreed.length,
          pending: res.pending.length,
          archive: res.archive.length,
        };
        setApplicationsPoints(points);
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
      })
      .finally(() => {
        setIsLoadingRoutes(false);
      });
  }

  // Скачать Ганта маршрутов кораблей
  function getGantt() {
    setIsLoading(true);
    mainApi
      .getGantt()
      .then((res) => {
        const file = window.URL.createObjectURL(res);
        window.location.assign(file);
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  // Скачать Ганта маршрутов ледокола
  function getIceGantt(id) {
    setIsLoading(true);
    mainApi
      .getIceGantt(id)
      .then((res) => {
        const file = window.URL.createObjectURL(res);
        window.location.assign(file);
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  // Закрыть попапы
  function closeAllPopups() {
    setIsPopupNewApplication(false);
  }

  // Select
  function changeOption(index, name, setSelected) {
    if (index === 0) {
      localStorage.removeItem(name);
    } else {
      localStorage.setItem(name, JSON.stringify(index));
    }
    setSelected(index);
  }

  // Определение падежа узлов
  function getNodeCases(num) {
    const str = String(num);
    const lastNum = Number(str[str.length - 1]);

    if (lastNum === 1 && (num < 10 || num > 20)) {
      return nodeCases[0];
    } else if (lastNum >= 2 && lastNum <= 4 && (num < 10 || num > 20)) {
      return nodeCases[1];
    } else {
      return nodeCases[2];
    }
  }

  // Вывод ледового класса
  function setIceClass(iceClass) {
    const arr = iceClass.split("_");
    const name = arr[0].toLowerCase();
    const str = name[0].toUpperCase() + name.slice(1);
    const num = arr[1];
    let newStr = " ";
    if (arr[2]) {
      for (let i = 2; i < arr.length; i++) {
        newStr += " " + arr[i];
      }
    }
    return `${str} ${num ? num : ""} ${newStr.toLowerCase()}`;
  }

  // Создание описания класса и скорости корабля
  function setInfoShip(shipClass, shipSpeed = 0) {
    const iceClass = shipClass ? setIceClass(shipClass) : "";
    const speed = shipSpeed ? getNodeCases(shipSpeed) : "";
    return `${iceClass}${iceClass && shipSpeed ? ", " : ""}${
      shipSpeed ? shipSpeed : ""
    } ${speed}`;
  }

  function addZero(number) {
    if (String(number).length === 1) {
      return `0${number}`;
    } else {
      return String(number);
    }
  }

  return (
    <>
      <CurrentUserContext.Provider value={{ currentUser }}>
        <div className="page">
          <Header loggedIn={loggedIn} handleLogout={handleLogout} />
          <main className="content">
            <Switch>
              <ProtectedRoute exact path="/icebreaker/" loggedIn={loggedIn}>
                <Main
                  getNavigationPoints={getNavigationPoints}
                  navPoints={navPoints}
                  getAllApplications={getAllApplications}
                  applicationsPoints={applicationsPoints}
                  allApplications={allApplications}
                  setIsPopupNewApplication={setIsPopupNewApplication}
                  addZero={addZero}
                  setInfoShip={setInfoShip}
                  shipRoute={shipRoute}
                  getShipRoute={getShipRoute}
                  getAllIcebreaker={getAllIcebreaker}
                  allIcebreakers={allIcebreakers}
                  setNewRouteList={setNewRouteList}
                  getGantt={getGantt}
                  getIceGantt={getIceGantt}
                  getIceRoute={getIceRoute}
                  iceRoute={iceRoute}
                  idIcebreaker={idIcebreaker}
                />
              </ProtectedRoute>

              <Route path="/icebreaker/signin">
                <Login
                  onSubmit={handleLogin}
                  isError={isError}
                  errorMessage={errorMessage}
                />
              </Route>

              <Route path="/icebreaker/signup">
                <Register
                  onSubmit={handleRegister}
                  isError={isError}
                  errorMessage={errorMessage}
                  changeOption={changeOption}
                />
              </Route>

              <Route path="*">
                <PageNotFound history={history} />
              </Route>
            </Switch>
          </main>

          {isPopupNewApplication && (
            <PopupNewApplication
              onClose={closeAllPopups}
              ships={ships}
              changeOption={changeOption}
              navPoints={navPoints}
              setNewApplication={setNewApplication}
              getShips={getShips}
              setInfoShip={setInfoShip}
              openPopupShip={() => {
                setIsPopupNewShip(true);
              }}
              addZero={addZero}
            />
          )}

          {isPopupNewShip && (
            <PopupNewShip
              onClose={() => {
                setIsPopupNewShip(false);
              }}
              iceClasses={iceClasses}
              getIceClasses={getIceClasses}
              changeOption={changeOption}
              setIceClass={setIceClass}
              setNewShip={setNewShip}
            />
          )}

          <Preloader isLoading={isLoading} />

          <PreloaderRoutes isLoadingRoutes={isLoadingRoutes} />
        </div>
      </CurrentUserContext.Provider>
    </>
  );
}

export default App;
