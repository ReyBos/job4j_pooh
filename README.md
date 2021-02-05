<h1>Pooh JMS</h1>

[![Build Status](https://travis-ci.org/ReyBos/job4j_pooh.svg?branch=master)](https://travis-ci.org/ReyBos/job4j_pooh) &nbsp;&nbsp;
<!--[![codecov](https://codecov.io/gh/ReyBos/job4j_pooh/branch/master/graph/badge.svg?token=X84OHVPF4U)](https://codecov.io/gh/ReyBos/job4j_pooh)-->

<h2>Техническое задание</h2>
<ul>
    <li>Нужно сделать аналог асинхронной очереди RabbitMQ.</li>
    <li>Приложение запускает Socket и ждет клиентов.</li>
    <li>Клиенты могут быть двух типов: отправители (publisher), получатели (subscriber).</li>
    <li>В качестве протокола будет использовать HTTP. Сообщения в формате JSON.</li>
    <li>Существуют два режима: queue, topic.</li>
    <li>В коде не должно быть синхронизации. Все нужно сделать на Executors и conccurent коллекциях.</li>
</ul>
<h4>Queue</h4>
<p>
    Отправитель посылает сообщение с указанием очереди.<br>
    Получатель читает первое сообщение и удаляет его из очереди. <br>
    Если приходят несколько получателей, то они читают из одной очереди. <br>
    Уникальное сообщение может быть прочитано, только одним получателем.
</p>
<p>
    <strong>Примеры запросов:</strong><br>
</p>
<p>
    POST
    <pre><code>http://localhost:9000/queue?data=msg</code></pre>
    в очередь queue будет добавлено сообщение "msg"<br><br>
<p>
    GET 
    <pre><code>http://localhost:9000/queue/weather</code></pre>
    из очереди queue будет извлечено одно сообщение
<h4>Topic</h4>
<p>
    Отправить посылает сообщение с указанием темы.<br>
    Получатель читает первое сообщение и удаляет его из очереди. <br>
    Если приходят несколько получателей, то они читают отдельные очереди.
</p>
<p>
    <strong>Примеры запросов:</strong><br>
</p>
<p>
    POST
    <pre><code>http://localhost:9000/topic?data=msg</code></pre>
    в очередь topic будет добавлено сообщение "msg"<br><br>
<p>
    GET 
    <pre><code>http://localhost:9000/topic/weather</code></pre>
    из очереди topic будет извлечено одно сообщение

<h2>Использованные средства</h2>
<p><a href="https://www.oracle.com/java/technologies/javase-jdk15-downloads.html">Open JDK 14</a> - компилятор\интерпритатор</p>
<p><a href="http://maven.apache.org/index.html">Maven</a> - сборка и управление проектом</p>

<h2>Компиляция</h2>
<pre>
<code>$ cd job4j_pooh
$ mvn package</code>
</pre>
Появится папка target, a в ней файл pooh.jar

<h2>Запуск</h2>
<pre>
<code>$ java -jar pooh.jar</code>
</pre>
<p>После запуска программы результат можно посмотреть в браузере, выполнив запросы из раздела "Техническое задание"</p>