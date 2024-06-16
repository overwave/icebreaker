const applications = [
    {
        id: 1,
        startDate: "2022-03-07",
        startPointId: 194,
        startPointName: "Точка старта",
        status: "PENDING",
        finishPointId: 224,
        finishPointName: "Точка финиша",
        shipId: 9,
        shipName: "Название корабля",
        shipClass: "Arc 4, 14 узлов"
    },
    {
        id: 2,
        startDate: "2022-03-07",
        startPointId: 194,
        startPointName: "Точка старта",
        status: "PENDING",
        finishPointId: 224,
        finishPointName: "Точка финиша",
        shipId: 9,
        shipName: "Название корабля",
        shipClass: "Arc 4, 14 узлов"
    }
];

const applicationsTest = [];

const allApplications = {
    pending: [
        {
            id: 1,
            startDate: "2022-03-07",
            startPointId: 194,
            startPointName: "Название точки старта",
            finishPointId: 224,
            finishPointName: "Название точки финиша",
            shipId: 9,
            shipName: "Название корабля",
            shipClass: "Arc 4, 14 узлов"
        },
        {
            id: 2,
            startDate: "2022-03-07",
            startPointId: 194,
            startPointName: "Название точки старта",
            finishPointId: 224,
            finishPointName: "Название точки финиша",
            shipId: 9,
            shipName: "Название корабля",
            shipClass: "Arc 4, 14 узлов"
        }
    ],
    agreed: [
        {
            id: 1,
            shipId: 9,
            shipName: "Название корабля",
            shipClass: "Arc 4, 14 узлов",
            convoy: true, // под сопровождением ледокола, false если не нужно сопровождение
            routes: [ // маршруты
                {
                    // кусок маршрута с сопровождением ледокола
                    id: 1,
                    startDate: "2022-03-07",
                    startPointId: 194,
                    startPointName: "Название точки старта",
                    finishDate: "2022-03-12",
                    finishPointId: 224,
                    finishPointName: "Название точки финиша",
                    icebreakerName: "Название сопровождающего ледокола",
                    icebreakerClass: "Класс сопровождающего ледокола"
                },
                {
                    // кусок маршрута без сопровождения ледокола
                    id: 2,
                    startDate: "2022-03-07",
                    startPointId: 194,
                    startPointName: "Название точки старта",
                    finishDate: "2022-03-12",
                    finishPointId: 224,
                    finishPointName: "Название точки финиша"
                }
            ]
        },
        {
            id: 2,
            shipId: 12,
            shipName: "Название корабля",
            shipClass: "Arc 4, 12 узлов",
            convoy: false, // без сопровождения ледокола, true если нужно сопровождение
            routes: [ // маршруты
                {
                    // кусок маршрута без сопровождения ледокола
                    id: 1,
                    startDate: "2022-03-07",
                    startPointId: 194,
                    startPointName: "Название точки старта",
                    finishDate: "2022-03-12",
                    finishPointId: 224,
                    finishPointName: "Название точки финиша"
                },
                {
                    // кусок маршрута без сопровождения ледокола
                    id: 2,
                    startDate: "2022-03-07",
                    startPointId: 194,
                    startPointName: "Название точки старта",
                    finishDate: "2022-03-12",
                    finishPointId: 224,
                    finishPointName: "Название точки финиша"
                }
            ]
        }
    ],
    archive: [
        // Тут можно всё то же самое, что и в agreed
    ]
};

module.exports = { applications, applicationsTest };
