const allIcebreakerRoutes = [
    {
        id: 1, // id Ледокола
        name: "50 лет победы",
        class: "ARC_9",
        speed: 22,
        route: [
            { // Часть маршрута ледокола
                id: 1, // id части маршрута
                isParking: false, // Не простой (стоянка)
                startDate: "2022-03-07",
                startPointId: 194,
                startPointName: "Название точки старта",
                finishDate: "2022-03-07",
                finishPointId: 224,
                finishPointName: "Название точки финиша",
                ships: [// Корабли, которые под сопровождением в этой части маршрута
                    {
                        shipId: 9,
                        shipName: "Название корабля",
                        shipClass: "Arc 4, 14 узлов"
                    },
                    {
                        shipId: 9,
                        shipName: "Название корабля",
                        shipClass: "Arc 4, 14 узлов"
                    }
                ]
            },
            { // Часть маршрута - простоя (стоянка)
                id: 2,
                isParking: true, // Простой (не двигается)
                startDate: "2022-03-07",
                startPointId: 194,
                startPointName: "Название точки старта",
                finishDate: "2022-03-10",
                finishPointId: 194,
                finishPointName: "Название точки финиша",
                ships: []
            }
        ]
    },
    {
        id: 2, // id Ледокола
        name: "Ямал",
        class: "ARC_9",
        speed: 21,
        route: [
            { // Часть маршрута ледокола
                id: 1, // id части маршрута
                isParking: false, // Не простой (стоянка)
                startDate: "2022-03-07",
                startPointId: 194,
                startPointName: "Название точки старта",
                finishDate: "2022-03-07",
                finishPointId: 224,
                finishPointName: "Название точки финиша",
                ships: [// Корабли, которые под сопровождением в этой части маршрута
                    {
                        shipId: 9,
                        shipName: "Название корабля",
                        shipClass: "Arc 4, 14 узлов"
                    },
                    {
                        shipId: 9,
                        shipName: "Название корабля",
                        shipClass: "Arc 4, 14 узлов"
                    }
                ]
            },
            { // Часть маршрута - простоя (стоянка)
                id: 2,
                isParking: true, // Простой (не двигается)
                startDate: "2022-03-07",
                startPointId: 194,
                startPointName: "Название точки старта",
                finishDate: "2022-03-07",
                finishPointId: 194,
                finishPointName: "Название точки финиша",
                ships: []
            }
        ]
    },
    {
        id: 3, // id Ледокола
        name: "Таймыр",
        class: "ARC_9",
        speed: 18.5,
        route: [
            { // Часть маршрута ледокола
                id: 1, // id части маршрута
                isParking: false, // Не простой (стоянка)
                startDate: "2022-03-07",
                startPointId: 194,
                startPointName: "Название точки старта",
                finishDate: "2022-03-07",
                finishPointId: 224,
                finishPointName: "Название точки финиша",
                ships: [// Корабли, которые под сопровождением в этой части маршрута
                    {
                        shipId: 9,
                        shipName: "Название корабля",
                        shipClass: "Arc 4, 14 узлов"
                    },
                    {
                        shipId: 9,
                        shipName: "Название корабля",
                        shipClass: "Arc 4, 14 узлов"
                    }
                ]
            },
            { // Часть маршрута - простоя (стоянка)
                id: 2,
                isParking: true, // Простой (не двигается)
                startDate: "2022-03-07",
                startPointId: 194,
                startPointName: "Название точки старта",
                finishDate: "2022-03-07",
                finishPointId: 194,
                finishPointName: "Название точки финиша",
                ships: []
            }
        ]
    },
    {
        id: 4, // id Ледокола
        name: "Вайгач",
        class: "ARC_9",
        speed: 18.5,
        route: [
            { // Часть маршрута ледокола
                id: 1, // id части маршрута
                isParking: false, // Не простой (стоянка)
                startDate: "2022-03-07",
                startPointId: 194,
                startPointName: "Название точки старта",
                finishDate: "2022-03-07",
                finishPointId: 224,
                finishPointName: "Название точки финиша",
                ships: [// Корабли, которые под сопровождением в этой части маршрута
                    {
                        shipId: 9,
                        shipName: "Название корабля",
                        shipClass: "Arc 4, 14 узлов"
                    },
                    {
                        shipId: 9,
                        shipName: "Название корабля",
                        shipClass: "Arc 4, 14 узлов"
                    }
                ]
            },
            { // Часть маршрута - простоя (стоянка)
                id: 2,
                isParking: true, // Простой (не двигается)
                startDate: "2022-03-07",
                startPointId: 194,
                startPointName: "Название точки старта",
                finishDate: "2022-03-07",
                finishPointId: 194,
                finishPointName: "Название точки финиша",
                ships: []
            }
        ]
    }
];

module.exports = { allIcebreakerRoutes };
