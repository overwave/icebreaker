import React from "react";
import "./PageNotFound.css";

function PageNotFound({ history }) {
  return (
    <section className="error">
      <div className="error__block">
        <div className="error__info">
          <h1 className="error__title">404</h1>
          <p className="error__subtitle">Кажется, вы заплыли не туда</p>
          <p className="error__text">Страница не найдена</p>
        </div>
        <nav className="error__nav">
          <button
            type="button"
            onClick={() => history.push("/icebreaker/")}
            className="error__btn content__btn"
          >
            Вернуться на главную
          </button>
        </nav>
      </div>
    </section>
  );
}

export default PageNotFound;
