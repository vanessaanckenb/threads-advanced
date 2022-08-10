<br/>

Em construção...

<br/>
<br/>
<br/>

# Threads

java 1.5 or + is needed.

java.util.concurrent

Estudo/resumo feito atraves do excelente curso Threads II do alura.

<br/>
<br/>

* Socket e TCP/IP

<br/>

* TCP (Transmission Control Protocol)

Para dois computadores se comunicarem, surgiram diversos protocolos que permitissem tal troca de informação. 

Através do TCP, é possível criar um fluxo entre dois ou mais computadores.

servidor <--TCP--> cliente

garante a entrega dos pacotes que transferimos

TCP não é um protocolo de aplicação e sim de transporte. Isso significa que não é preciso se preocupar em como os dados serão transmitidos. O TCP garante que os dados serão transmitidos de maneira confiável, mas não se preocupa com o significado desses dados.

TCP garante que os dados são transmitidos e o protocolo de aplicação define o significado desses dados

protocolo de aplicação = através dele, dependendo do protocolo, como o HTTP ou o FTP, podemos definir que queremos acessar um arquivo no servidor, enviar parâmetros de pesquisa ou submeter dados de um formulário.

<br/>
<br/>

-----------------------------------------------------------

<br/>
<br/>

* Portas

objetivo = estabelecer uma conexão, diversos clientes podendo se conectar a um só servidor. 

Cada cliente vai manter uma conexão com o servidor, mas como o servidor saberá distinguir entre os clientes?

Assim como existe o IP para identificar uma máquina, a porta é a solução para identificar diversos clientes em uma máquina. 

Esta porta é um número de 2 bytes, varia de 0 a 65535. Se todas as portas de uma máquina estiverem ocupadas, não é possível se conectar a ela enquanto nenhuma for liberada. Então, além do IP, também é preciso saber a porta!

<br/>
<br/>

-----------------------------------------------------------

<br/>
<br/>

* Socket - Criando um servidor

Todos esses detalhes do protocolo (TCP, IP da máquina servidora e a porta) são abstraídos no mundo Java através de um socket. 

Um socket é o ponto-final de um fluxo de comunicação entre duas aplicações, através de uma rede. 

Servidor - ServerSocket localhost:12345 <----------> Cliente - Socket

Vamos primeiro implementar o servidor, usando as classes do pacote java.net. 

Primeiro passo é criar o ServerSocket. Ao criar o ServerSocket, precisamos definir a porta. Há algumas portas já pré-definidas no sistema operacional. Por exemplo, a porta 22 é reservada para o SSH, 20 para o FTP, 80 para o HTTP etc. Normalmente, escolhendo um porta maior do que 1023, não devemos entrar em conflito com portas já pré-definidas.

```
ServerSocket serverSocket = new ServerSocket(1234);
```

Acima criamos apenas um objeto ServerSocket, ainda não podemos aceitar uma conexão. Para tal, devemos chamar o método accept

```
Socket socket = servidor.accept();
```

O método accept é bloqueante e trava a thread principal. Ou seja, ao rodar, a thread main fica parada até receber uma conexão através de um cliente

<br/>
<br/>

-----------------------------------------------------------

<br/>
<br/>

* Socket - Criando um cliente

```
Socket socket = new Socket("localhost", 12345);
```

Com o nosso servidor rodando, já podemos estabelecer uma conexão.

Após rodar o cliente, a máquina virtual do cliente terminou. Nosso servidor também parou de rodar, pois só aceita um cliente.

<br/>
<br/>

-----------------------------------------------------------

<br/>
<br/>

* Aceitando vários clientes

O servidor precisa chamar o método accept para cada cliente, se temos apenas um accept, quando um cliente estabelece uma conexão a jvm é finalizada.

Ao estabelecer uma conexão, a porta muda para cada cliente = a comunicação é estabelecida através de uma porta inicial, mas após isso cada cliente usa a sua própria porta

Usamos a porta 12345 para criar a conexão inicial, toda comunicação a partir desse momento é feita com uma porta dedicada pra cada cliente.

A porta no lado do servidor ainda é porta 12345! Toda comunicação TCP envolve dois pontos finais: um socket de cada lado com seu próprio endereço e porta: [endereço_servidor:porta_servidor] <-> [endereço_cliente:porta_do_cliente]

#Acceptiong client on port 63900

#Acceptiong client on port 63906



Caso algum dos clientes execute uma tarefa pesada, que requer grande tempo de CPU, os outros clientes ficarão bloqueados e impedidos de interagir com o servidor. A solução para isto é criar uma Thread para cada cliente, assim, mesmo que um cliente execute uma tarefa pesada, apenas a execução de sua thread ficará travada enquanto ele processa a tarefa, sem atrapalhar os outros clientes.

No lado do servidor devemos usar para cada cliente uma nova thread, porque o método accept da classe ServerSocketé bloqueante

<br/>
<br/>

-----------------------------------------------------------

<br/>
<br/>

* Reaproveitando threads - Pool

Criamos para cada novo cliente, uma nova thread dedicada. 

10 clientes = 10 threads, proporcionalmente. 

Uma thread é mapeada para uma thread nativa do sistema operacional, isso tem o seu custo, sempre devemos ter cuidado e pensar antecipadamente quantas threads a nossa aplicação pode criar para melhorar o uso dos recursos.

o Java já vem preparado para reaproveitar as threads através de um pool. 

Um pool = um gerenciador de objetos. Ele possui um limite de objetos que podemos estabelecer. Além disso, podemos reaproveitar esses objetos! Quem conhece um pool de conexões do mundo de banco de dados, é exatamente isso que queremos utilizar para o mundo de threads.

É um gerenciador de objetos do tipo thread, que é capaz de limitar a quantidade de threads além de fazer um reaproveitamento das mesmas.

gerencia uma quantidade de threads estabelecida, que fica aguardando por tarefas fornecidas pelos clientes. A sua grande vantagem é que além de controlarmos a quantidade de threads disponível para uso dos clientes, também podemos fazer o reuso de threads por clientes diferentes, não tendo o gasto de CPU de criar uma nova thread para cada cliente que chega no servidor.

Para usar um pool de threads, devemos utilizar a classe Executors que possui vários métodos estáticos para criar o pool específico. 

```
ExecutorService poolDeThreads = Executors.newFixedThreadPool(5); 
poolDeThreads.execute(distribuirTarefas);
``` 

ExecutorService é uma interface e a classe Executors devolve uma implementação do pool, através dos métodos newFixedThreadPool(5), newCachedThreadPool() ou newSingleThreadExecutor().

Também podemos dizer que a classe Executors é uma fábrica de pools.

método newFixedThreadPool = cria um pool com uma quantidade de threads pré-definida

Assim nunca teremos mais do que 5 threads na aplicação e elas serão reaproveitadas

Se precisarmos de mais uma thread, o service bloqueia a execução e espera até que um outro cliente devolva uma thread.

Quando rodarmos o nosso servidor e conectarmos mais de 5 clientes, o proximo cliente fica bloqueado e a saída não aparece no console do servidor, ate que um dos 5 clientes liberem a thread.

newFixedThreadPool é o pool de threads em que definimos previamente a quantidade de threads que queremos utilizar. Assim, se por exemplo estabelecermos que queremos no máximo 4 threads, este número nunca será extrapolado e elas serão reaproveitadas.

-

newCachedThreadPool é o pool de threads que cresce dinamicamente de acordo com as solicitações. É ideal quando não sabemos o número exato de quantas threads vamos precisar. O legal deste pool é que ele também diminuí a quantidade de threads disponíveis quando uma thread fica ociosa por mais de 60 segundos.

Quando não sabemos exatamente qual é o número ideal para o nosso pool, pode fazer sentido usar um pool que cresce dinamicamente. 

A quantidade de threads cresce à medida que a demanda aumenta. 

O pool também diminui a quantidade quando uma thread fica ociosa mais de 60 segundos.

Ao tentar a execução novamente com vários clientes, nenhum deles será bloqueado porque o pool cresce dinamicamente.

```
ExecutorService poolDeThreads = Executors.newCachedThreadPool();
poolDeThreads.execute(distribuirTarefas); 
```

-

newSingleThreadExecutor é o pool de threads que só permite uma única thread.


```
ExecutorService poolDeThreads = Executors.newSingleThreadExecutor();
poolDeThreads.execute(distribuirTarefas);
```

<br/>
<br/>

-----------------------------------------------------------

<br/>
<br/>

ExecutorService estende uma outra interface Executor que possui apenas um método, o execut

Executor
   /\
   |
   | extends
   |
ExecutorService
   /\
   |
   | extends
   |
ScheduledExecutorService 


```
Executor pool = Executors.newCachedThreadPool();
pool.execute(distribuirTarefas);
```

a interface executor possui apenas o metodo execut.

```
ExecutorService pool = Executors.newCachedThreadPool();
```

temos métodos específicos da interface ExecutorService como o submit e shutdown

```
ScheduledExecutorService pool = Executors.newCachedThreadPool();
pool.scheduleAtFixedRate(tarefa, 0, 60, TimeUnit.MINUTES); //executamos uma tarefa a cada 60 minutos
```

Através desse pool podemos agendar e executar uma tarefa periodicamente, por exemplo:

<br/>
<br/>

-----------------------------------------------------------

<br/>
<br/>

A classe Thread possui um método estático getAllStackTraces que devolve um conjunto com todas as threads da JVM.

```
Set<Thread> todasAsThreads = Thread.getAllStackTraces().keySet();

for (Thread thread : todasAsThreads) {
    System.out.println(thread.getName());
}
```

Também podemos "perguntar" quantos processadores temos disponíveis.

```
Runtime runtime = Runtime.getRuntime();
int qtdProcessadores = runtime.availableProcessors();
System.out.println("Qtd de processadores: " + qtdProcessadores);
```

<br/>
<br/>

-----------------------------------------------------------

<br/>
<br/>

```
Socket socket = new Socket("localhost", 1234);
System.out.println("###Connected###");

new Thread(new SendMessageTask(socket)).start();
new Thread(new ReceiveMessageTask(socket)).start();

socket.close();
```

java.net.SocketException: Socket is closed

O problema é o seguinte: estamos inicializando cada thread corretamente, mas o Socket é fechado na thread principal (main). Quando estamos começando a enviar e receber dados, é provável que a thread main já tenha fechado o Socket

Devemos parar a thread main e poderíamos utilizar novamente o método Thread.sleep(..). Mas qual seria o tempo adequado para esperar? Além disso, já temos um critério de interrupção. Na hora de fazer a leitura, quando apertamos apenas ENTER, aí sim devemos parar.

Para resolver o nosso problema, podemos indicar à thread main esperar a execução enquanto a thread de leitura está rodando. Isso é feito através do método join


```
final var sendMessageThreads = new Thread(new SendMessageTask(socket));
final var receiveMessageThreads = new Thread(new ReceiveMessageTask(socket));

sendMessageThreads.start();
receiveMessageThreads.start();

//thread main vai esperar essa thread finalizar (finaliza quando da enter em branco)
sendMessageThreads.join();

socket.close();
```

Quando a thread main executa o método join, ela sabe que precisa esperar a execução da thread que envia os comandos . A thread main ficará esperando até a outra thread acabar.

Quando uma thread t2 chama t1.join(), significa que t2 vai esperar t1 finalizar.

t2 vai se "juntar" ao t1, isso é esperar a finalização do t1.


Por mais que o uso de um pool não invalide nossa solução, como o cliente terá sempre duas threads, uma para receber e outra para enviar dados, o pool não se faz necessário. Veja que é uma situação bem diferente do nosso servidor, pois não sabemos quantos clientes se conectarão. Além disso, no servidor realmente queremos reutilizar as threads, no cliente não.

thread.join(30000);


Isso significa que vamos esperar 30s para se "juntar" a outra thread. Depois dos 30s continuaremos, mesmo se a outra thread não tiver finalizado ainda.
