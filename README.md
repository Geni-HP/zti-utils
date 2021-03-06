# ConsoleScanner #
Полезный класс, который позволяет быстро и легко создавать консольные приложения в Java.

Для того, чтобы начать использовать **ConsoleScanner** достаточно просто создать объект, и передать ему в качестве параметров _префикс команды_ и _префикс параметра_ 
```java
ConsoleScanner scanner = new ConsoleScanner("/", "-");
```
В таком виде сканнер будет работать _синхронно_ с приложением, и считать ввод вы сможете с помощью вызова метода ```scanner.scan()```. Если же вызвать конструктор с boolean параметром  ```false```, то сканнер будет работать в асинхронном режиме, и все команды, которые в него добавлены, будут выполняться в отдельном потоке:
```java
ConsoleScanner scanner = new ConsoleScanner("/", "-", false);
```
В случае использования асинхронного метода для начала сканнирования вызовите на объектре сканнера метод ```scanner.start()```, а для остановки ```scanner.stop()```.
### Добавление команды в сканнер ###
Внутри класса ConsoleScanner имеется список добавленных в него команд, которые представлены классом ```Command```. Для добавление новой команды в сканнер создайте экземпляр класса _Command_, и в качестве параметров передайте ему имя команды и краткое описание.
```java
Command command = new Command("test", "This is a test command");
```
Затем добавьте команду в сканнер вызвав на последнем метод ```scanner.addCommand(command)```. Сканнер сам подхватит ее во время работы (синхронной или нет).

### Создание логики для команды ###
После того, как вы создали и добавили в сканнер команду стоит задать для нее логику выполнения. В ходе своей работы сканнер читает входящий поток, и, при совпадении с командой на входе, он разбирает входящую строку на параметры исподьзуя данные о префиксе параметров, переданные ему через конструктор. Пример для команды test: ```/test -n Name -s Surname```, где _-n_ и _-s_ параметры команды, а _Name_ и _Surname_ значения этих параметров.
Так как одному параметру может соответствовать несколько значений сканнер складывает каждое значение для параметра во внутренний список ```List<String> values;```, затем создает карту ```Map<String, List<String>> params;``` и используя имя параметра как ключь заносит все списки в соответствующие места в карте. После чего передает управление внутреннему методу, который передает текущую карту параметров на выполнение объекту, который расширяет интерфейс ```Consumable``` и находится внутри объекта соответствующей команды. Все, что остается программисту - это определить поведение этого самого объекта _Consumable_

Для того, чтобы определить объект расширяющий интерфейс _Consumable_ нужно вызвать на команде метод ```setConsumer(Consumable consumable)```. Пример кода:
```java
command.setConsumer(new Consumable() {
    @Override
    public void consume(Map<String, List<String>> map) {
      //Логика команды
    }
});
```
Естественно, вместо анонимного класса можно использовать внутренний, или использовать лямбду:
```java 
command.setConsumer(map -> {
    //Логика команды
});
```
При этом передоваемая методу _consume_ карта - это набор параметров и их значений в соответствии со спецификациями карты. Иногда возникает необходимость при вызове команды передовать ей значения без параметров ```/test something```. В таком случает в сканнере предусмотрен ключь по умолчанию, который будет использоват для помещения значений в карту параметров. Значение этого ключа можно получить и задать программно.

Стоит отметить, что метод ```setConsumer(Consmable consumable);``` возвращает сам себя (объект типа _Command_), и его можно вызывать в ходе инициализации экземпляра класса _Command_ ```scanner.addCommand(new Command("test, "").setConsumer(new Consumable()...));```

### Дополнительные сведения ###

Метод ```scanner.setWelcome(String s)``` позволяет задать сообщение приветствия, которое появляется автоматически при асинхронной работе сканера. Если вы не хотите, чтобы оно отображалось - просто передайте в качетве параметра пустые скобки строки "".

Методы ```scanner.getCommandPrefix()``` и ```scanner.setCommandPrefix(String prefix)``` можно использовать для получения строки префикса команды, и для ее установки вне конструктора соответственно.

Методы ```scanner.getParamPrefix()``` и ```scanner.setParamPrefix(String prefix)``` можно использовать для получения строки префикса параметра, и для ее установки вне конструктора соответственно.

Методы ```scanner.getDefaultKey()``` и ```scanner.setDefaultKey(String key)``` можно использовать для получения строки ключа по умолчанию, и для ее установки соответственно.

### Пример использования ###
Синхронная программа пересмешник:
```java
import by.zti.main.scanner.Command;
import by.zti.main.scanner.ConsoleScanner;

public class Main {

    public static void main(String[] args) {
        ConsoleScanner scanner = new ConsoleScanner("/", "-");
        scanner.addCommand(new Command("say", "").setConsumer(map -> map.get(scanner.getDefaultKey()).forEach(System.out::println)));
        scanner.scan();
    }
}
```
Вывод:
```java
/say Hello
Hello

Process finished with exit code 0
```

Асинхронная программа пересмешник с командой для выхода:
```java
import by.zti.main.scanner.Command;
import by.zti.main.scanner.ConsoleScanner;

public class Main {

    public static void main(String[] args) {
        ConsoleScanner scanner = new ConsoleScanner("/", "-", false);
        scanner.setWelcome("");
        scanner.addCommand(new Command("say", "").setConsumer(map -> map.get(scanner.getDefaultKey()).forEach(System.out::println)));
        scanner.addCommand(new Command("exit", "").setConsumer(map -> scanner.stop()));
        scanner.start();
    }
}
```
Вывод: 
```java 
/say Hello
Hello
/exit

Process finished with exit code 0
```
# Serializer #

Класс, реализующий процесс сериализации объектов, расширяющих интерфейс _Serializable_. 

Для того, чтобы использовать сереализатор, необходимо создать экземпляр класса, и указать тип данных с которым он будет работать, а в качестве параметра передать путь к фалу в который (из которого) будет произведена сериализация\десериализация, или объект типа _File_.
```java
Serializer<Object> serializer = new Serializer<>(String filePath);
```
Вместо типа данных _Object_ укажите тип данных, с которым будет работать данный экземпляр

### Операции сереализации и десереализации ###

Для того, чтобы провести операцию сереализации достаточно вызвать на объекте метод ```serializer.serialaize(Object obj)```, котоырй сереализирует объект в файл по пути заданному в конструкторе при инициализации, или в файл переданный в конструктор.
Пример кода сереализации строки в файл:
```java
import by.zti.main.serializer.Serializer;

public class Main {
    public static void main(String[] args) {
        Serializer<String> serializer = new Serializer<>("test.ser");
        serializer.serialize("Test string");
    }
}
```
В результате выполнения кода в корне программы появится файл с названием "test", и расширением "ser" (test.ser).

Для того, чтобы десереализовать объект из файла по пути, или файла переданного в конструктор при инициализации достаточно просто вызвать на сериализаторе метод ```serializer.deserialize()```, котоырй вернет объект типа соответствующего тому, который был задан при объявлении сериализатора.
Пример десериализации строки из файла:
```java
import by.zti.main.serializer.Serializer;

public class Main {
    public static void main(String[] args) {
        Serializer<String> serializer = new Serializer<>("test.ser");
        System.out.println(serializer.deserialize());
    }
}
```
Вывод:
```java
Test string

Process finished with exit code 0
```
# How to get | Где достать #

В Maven Central разумеется :D

```
<dependency>
    <groupId>com.github.cvazer</groupId>
    <artifactId>zti-utils-scanner</artifactId>
    <version>1.1.0</version>
</dependency>

<dependency>
    <dependency>
    <groupId>com.github.cvazer</groupId>
    <artifactId>zti-utils-serializer</artifactId>
    <version>1.1.0</version>
</dependency>

<dependency>
    <groupId>com.github.cvazer</groupId>
    <artifactId>zti-utils-incubator</artifactId>
    <version>1.1.0</version>
</dependency>

<dependency>
    <groupId>com.github.cvazer</groupId>
    <artifactId>zti-utils-legacy</artifactId>
    <version>1.1.0</version>
</dependency>
```
