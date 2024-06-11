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

function App() {

  // Ошибки
  const [isError, setIsError] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  // Загрузка
  const [isLoading, setIsLoading] = useState(false);

  // Пользователь
  const [loggedIn, setLoggedIn] = useState(localStorage.getItem("loggedIn") ? localStorage.getItem("loggedIn"):false);
  const [currentUser, setCurrentUser] = useState({
    role: "",
    login: ""
  });

  // Navigation points
  const [navPoints, setNavPoints] = useState([]);

  const history = useHistory();
  const { pathname } = useLocation();

  useEffect(() => {
    if (loggedIn && (pathname === "/signin" || pathname === "/signup")) {
      history.push("/");
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
        console.log(res);
        localStorage.clear();
        setCurrentUser({
          role: "",
          login: ""
        });
        setLoggedIn(false);
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
        createError(errorsRegister, err.status);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  function getUserInfo() {
    setIsLoading(true);
    mainApi
      .getUserInfo()
      .then((res) => {
        setLoggedIn(true);
        setCurrentUser(res);
      })
      .catch((err) => {
        //handleLogout();
        console.log(err);
        console.log(`Ошибка: ${err}`);
        createError(errorsLogin, err.status);
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
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  // Список кораблей
  function getShips() {
    setIsLoading(true);
    mainApi
      .getShips()
      .then((res) => {
        console.log(res);
      })
      .catch((err) => {
        console.log(`Ошибка: ${err}`);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  function closeAllPopups() {
    
  }

  return (
    <>
      <CurrentUserContext.Provider value={{ currentUser }}>
        <div className="page">
          <Header loggedIn={loggedIn} handleLogout={handleLogout} />
          <main className="content">
            <Switch>
              <ProtectedRoute exact path="/" loggedIn={loggedIn}>
                <Main getNavigationPoints={getNavigationPoints} navPoints={navPoints} getShips={getShips} />
              </ProtectedRoute>

              <Route path="/signin">
                <Login
                  onSubmit={handleLogin}
                  isError={isError}
                  errorMessage={errorMessage}
                />
              </Route>

              <Route path="/signup">
                <Register
                  onSubmit={handleRegister}
                  isError={isError}
                  errorMessage={errorMessage}
                />
              </Route>

              <Route path="*">
                <PageNotFound history={history} />
              </Route>
            </Switch>
          </main>

          <Preloader isLoading={isLoading} />
        </div>
      </CurrentUserContext.Provider>
    </>
  );
}

export default App;
