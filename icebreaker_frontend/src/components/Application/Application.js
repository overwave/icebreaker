import "./Application.css";
import ship from "../../images/ship.svg";
import download from "../../images/download.svg";
import arrow from "../../images/arrow.svg";

export default function Application() {
    return (
        <li className="application">
            <div className="application__header">
                <div className="application__header-left">
                    <p className="application__title">ДЮК II</p>
                    <p className="application__points">Arc 4, 14 узлов</p>
                </div>
                <div className="application__btns">
                    <button className="application__btn application__btn_name_ship" type="button">
                        <img className="application__icon" src={ship} alt="Посмотреть маршрут" />
                    </button>
                    <button className="application__btn application__btn_name_download" type="button">
                        <img className="application__icon" src={download} alt="Скачать заявку" />
                    </button>
                </div>
            </div>
            <div className="application__route">
                <div className="application__point">
                    <span className="application__point-name">Новый порт</span>
                    <span className="application__point-date">12 июня</span>
                </div>
                <div className="application__arrow">
                    <img className="application__arrow-img" src={arrow} alt="" />
                </div>
                <div className="application__point">
                    <span className="application__point-name">Рейд Мурманска</span>
                    <span className="application__point-date">24 июня</span>
                </div>
            </div>
        </li>
    );
}