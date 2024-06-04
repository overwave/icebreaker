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

function App() {

  // Ошибки
  const [isError, setIsError] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  // Загрузка
  const [isLoading, setIsLoading] = useState(false);

  // Пользователь
  const [loggedIn, setLoggedIn] = useState(false);
  const [currentUser, setCurrentUser] = useState({
    name: "",
    email: "",
    _id: "",
  });

  const history = useHistory();
  const { pathname } = useLocation();

  useEffect(() => {
    if (loggedIn && (pathname === "/signin" || pathname === "/signup")) {
      history.push("/movies");
    }
    setIsError(false);
  }, [loggedIn, history, pathname]);

  useEffect(() => {
    checkToken();
  }, []);

  function createError(errorsList, err) {
    if (errorsList[err] !== undefined) {
      setErrorMessage(errorsList[err]);
    } else {
      setErrorMessage(errors[500]);
    }
    setIsError(true);
  }

  function handleLogin({ email, password }) {
    setIsLoading(true);
    mainApi
      .authorize({ email, password })
      .then((res) => {
        localStorage.setItem("jwt", res.token);
        setLoggedIn(true);
      })
      .catch((err) => {
        createError(errorsLogin, err.status);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  function handleRegister({ name, email, password }) {
    setIsLoading(true);
    mainApi
      .register({ name, email, password })
      .then(() => {
        handleLogin({ email: email, password: password });
      })
      .catch((err) => {
        createError(errorsRegister, err.status);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }

  function handleLogout() {
    localStorage.clear();
  }

  function checkToken() {
    const jwt = localStorage.getItem("jwt");
    if (jwt) {
      mainApi
        .getContent(jwt)
        .then((res) => {
          setCurrentUser(res);
          setLoggedIn(true);
          history.push(pathname);
        })
        .catch((err) => {
          handleLogout();
          console.log(`Ошибка: ${err}`);
        });
    }
  }

  return (
    <>
      <CurrentUserContext.Provider value={{ currentUser }}>
        <div className="page">
          <main className="content">
            <Switch>
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
