<h1>Pooh JMS</h1>

[![Build Status](https://travis-ci.org/ReyBos/job4j_pooh.svg?branch=master)](https://travis-ci.org/ReyBos/job4j_pooh) &nbsp;&nbsp;
[![codecov](https://codecov.io/gh/ReyBos/job4j_pooh/branch/master/graph/badge.svg?token=X84OHVPF4U)](https://codecov.io/gh/ReyBos/job4j_pooh)

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
    <strong>POST /queue</strong>
<pre><code>{
    "queue" : "weather",
    "text" : "temperature +18 C"
}</code></pre>
<p>
    <strong>GET /queue/weather</strong>
<pre><code>{
    "queue" : "weather",
    "text" : "temperature +18 C"
}</code></pre>
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
    <strong>POST /topic</strong>
<pre><code>{
    "queue" : "weather",
    "text" : "temperature +18 C"
}</code></pre>
<p>
    <strong>GET /topic/weather</strong>
<pre><code>{
    "queue" : "weather",
    "text" : "temperature +18 C"
}</code></pre>