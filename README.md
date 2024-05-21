# RecommendationSystem

![GitHub Release](https://img.shields.io/github/v/release/AtriLux/recommendSystem?color=darkred)
![GitHub commit activity](https://img.shields.io/github/commit-activity/t/AtriLux/recommendSystem?color=orange)
![GitHub last commit](https://img.shields.io/github/last-commit/AtriLux/recommendSystem?color=yellow)
![GitHub top language](https://img.shields.io/github/languages/top/AtriLux/recommendSystem?color=darkgreen)
![GitHub Downloads (all assets, all releases)](https://img.shields.io/github/downloads/AtriLux/recommendSystem/total?color=blue)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/AtriLux/recommendSystem?color=darkblue)

- [EN](#eng)
- [RUS](#rus)

# <a name="eng">English</a>

- [Description](#description)
- [Connection](#connection)
- [Initialization](#initialization)
- [Usage](#usage)

## <a name="description">Description</a>

The recommendation system is a universal software module designed as part of the final qualification work of the Bachelor of NSTU in 2024.

You can use it to improve your application by implementing recommendations in it. The module itself will create all the necessary tables in the database, you only need to configure the initial parameters, bring the objects to a unified appearance and add places in the code to add objects to the user profile and receive recommendations. A brief description is provided below.

Technologies used:
- Java
- Hibernate

Implemented algorithms:
- Frequent Mining.
- Vectorization (aka Custom) is a new method developed as part of the final qualification work.

## <a name="connection">Connection</a>

1. Download the jar-file and connect it as a library
```html
<dependency>
<groupId>org.demo</groupId>
<artifactId>recommend-system</artifactId>
<version>1.2</version>
<scope>system</scope>
<systemPath>${project.basedir}/recommend-system-1.2.jar</systemPath>
</dependency>
```
2. Connect via package
```html
<dependency>
<groupId>org.demo</groupId>
<artifactId>recommend-system</artifactId>
<version>1.2</version>
</dependency>
```

## <a name="initialization">Initialization</a>

To get started, create an object of the `RecommendationSystem` class. Send the required parameters to the constructor:
- connecting to the database via `Hibernate` is the same as what you use yourself, the types `EntityManager`, `EntityManagerFactory`.
- a set of characteristics and their ranges - they will be further analyzed to make recommendations, transmitted in the form of a `HashMap`.
- user ID - the current profile is determined by it.  

Optional parameters:
- the order of templates for the template method - determines the number of characteristics from which the template will be compiled. The more, the fewer templates there will be, but the more accurate they will be. The recommended value is 2 or 3.
- similarity coefficient - determines how identical objects are recommended: 1 ↔️ identical, 0 ↔️ any. The recommended value is in the range from 0.2 to 0.5.
- the type of algorithm used - the template method is used by default.

Example of initialization:
```java
AlgorithmType algorithmType = AlgorithmType.None;
int order = 2;
double similarity = 0.5;
Map<String, Integer> rangeCnt = new HashMap<>(){{
put("size", 10);
put("hlwordCnt", 4);
put("titleLen", 8); }};
recommendationSystem = new RecommendationSystem(AppManager.getUser().getId(),
                                                rangeCnt, order, similarity, algorithmType,
                                                HibernateUtils.getEntityManagerFactory(),
                                                HibernateUtils.getEntityManager());
```
In the future, all parameters can be changed using setters. It is not recommended changing the analyzed characteristics, this may lead to errors.  

If you need to clear information about the current profile, use the `clearProfile` method.

## <a name="usage">Usage</a>

The system works with unified objects of the type `RecommendationObject`. To create an object, you need to create a `HashMap` with the same characteristics that were sent during the initialization of the recommendation system. You also need to specify the object ID. This is a software field that is not used in the database, so you can set it as a dummy or as a software identifier for your own purposes.  

Example of creation:
```java
int id = -1;
Map<String, String> strParams = new HashMap<>();
Map<String, Integer> numParams = new HashMap<>();

numParams.put("titleLen", 40);
numParams.put("size", 100);
numParams.put("hlwordCnt", 2);

RecommendationObject object = new RecommendationObject(id, strParams, numParams);
```
If the object needs to change an existing user profile, then use the `addObject` method. You can specify the weight of an object for the vectorization method: the greater the weight, the more important the object is.
If you want to get a recommendation for an object based on the existing rules in the user profile, then use one of the following methods:
- `isObjectRecommend` - processes a single object, returns true if the object is recommended.
- `filterRecommendObjects` - processes the `ArrayList` of objects, returns an `ArrayList` that does not contain objects that are not recommended.
- `sortRecommendObjects` - processes the `ArrayList` of objects, returns a sorted `ArrayList`, where the first is the most recommended object, and the last is the most not recommended.

# <a name="rus">Русский</a>
- [Описание](#описание)
- [Подключение](#подключение)
- [Инициализация](#инициализация)
- [Использование](#использование)

## <a name="описание">Описание</a>

Рекомендательная система - это универсальный программный модуль, выполненный в рамках выпускной квалификационной работы бакалавра НГТУ в 2024 году.  

С его помощью можно улучшить ваше приложение, внедрив в него рекомендации. Модуль сам создаст все необходимые таблицы в базе данных, вам необходимо только настроить начальные параметры, привести объекты к унифицированному виду и добавить в коде места для добавления объектов в профиль пользователя и получения рекомендаций. Краткое описание представлено ниже.  

Используемые технологии:
- Java
- Hibernate

Реализованные алгоритмы:
- Метод шаблонов - Frequent Mining.
- Метод векторизации - Custom - новый метод, разработанный в рамках выпускной квалификационной работы.

## <a name="подключение">Подключение</a>

1. Скачать jar-файл и подключить как библиотеку
```html
<dependency>
  <groupId>org.demo</groupId>
  <artifactId>recommend-system</artifactId>
  <version>1.2</version>
  <scope>system</scope>
  <systemPath>${project.basedir}/recommend-system-1.2.jar</systemPath>
</dependency>
``` 
2. Подключить через package
```html
<dependency>
  <groupId>org.demo</groupId>
  <artifactId>recommend-system</artifactId>
  <version>1.2</version>
</dependency>
```
## <a name="инициализация">Инициализация</a>
Для начала работы создайте объект класса `RecommendationSystem`. Отправьте в конструктор обязательные параметры:
- подключение к БД через `Hibernate` - то же, которое используете сами, типы `EntityManager`, `EntityManagerFactory`.
- набор характеристик и их диапазоны - их в дальнейшем будут анализировать для составления рекомендаций, передается в виде `HashMap`.
- идентификатор пользователя - по нему определяется текущий профиль.
  Необязательные параметры:
- порядок шаблонов для метода шаблонов - определяет количество характеристик, из которых будет составлен шаблон. Чем больше - тем меньше будет шаблонов, но тем точнее они окажутся. Рекомендуемое значение 2 или 3.
- коэффициент сходства - определяет, насколько идентичные объекты рекомендовать: 1 -> идентичные, 0 -> любые. Рекомендуемое значение в диапазоне от 0.2 до 0.5.
- тип используемого алгоритма - по умолчанию используется метод шаблонов.

Пример инициализации:
```java
AlgorithmType algorithmType = AlgorithmType.None;
int order = 2;
double similarity = 0.5;
Map<String, Integer> rangeCnt = new HashMap<>(){{
                                     put("size", 10);
                                     put("hlwordCnt", 4);
                                     put("titleLen", 8); }};
recommendationSystem = new RecommendationSystem(AppManager.getUser().getId(),
                                                rangeCnt, order, similarity, algorithmType,
                                                HibernateUtils.getEntityManagerFactory(),
                                                HibernateUtils.getEntityManager());
```
В дальнейшем все параметры можно изменить при помощи сеттеров. Не рекомендуется менять анализируемые характеристики, это может привести к ошибкам.
Если требуется очистить информацию о текущем профиле, используйте метод `clearProfile`.

## <a name="использование">Использование</a>

Система работает с унифицированными объектами типа `RecommendationObject`. Для создания объекта нужно создать `HashMap` с такими же характеристиками, которые были отправлены при инициализации рекомендательной системы. Также нужно указать идентификатор объекта. Это программное поле, которое не используется в БД, поэтому вы можете задать его как пустышку или как программный идетификатор для своих целей.  

Пример создания:
```java
int id = -1;
Map<String, String> strParams = new HashMap<>();
Map<String, Integer> numParams = new HashMap<>();

numParams.put("titleLen", 40);
numParams.put("size", 100);
numParams.put("hlwordCnt", 2);

RecommendationObject object = new RecommendationObject(id, strParams, numParams);
```   
Если объект должен изменить существующий профиль пользователя, то используйте метод `addObject`. Вы можете указать вес объекта для метода векторизации: чем больше вес, тем более важным является объект.  

Если вы хотите получить рекомендацию по объекту, отталкиваясь от существующих правил в профиле пользователя, то используйте один из следующих методов:
- `isObjectRecommend` - обрабатывает один объект, возвращает true, если объект рекомендуется.
- `filterRecommendObjects` - обрабатывает `ArrayList` объектов, возвращает `ArrayList`, в котором нет объектов, которые не рекомендуются.
- `sortRecommendObjects` - обрабатывает `ArrayList` объектов, возвращает отсортированный `ArrayList`, где первым является самый рекомендованный объект, а последним - самый нерекомендованный.