import "./PreloaderRoutes.css";

export default function PreloaderRoutes({ isLoadingRoutes }) {
  return (
    <>
      {isLoadingRoutes && (
        <div className="preloader-routes">
          <div className="preloader-routes__container">
            <div className="preloader-routes__round"></div>
            <h2 className="preloader-routes__title">Маршрут формируется</h2>
            <p className="preloader-routes__text">
              Пожалуйста, не обновляйте страницу
            </p>
            <p className="preloader-routes__text">
              Формирование маршрута займёт от 1 до 5 минут
            </p>
          </div>
        </div>
      )}
    </>
  );
}
