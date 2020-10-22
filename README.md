# jav-eas-reader-db-parameterization

### Proceso de instalación

#### Requisitos.

* Instalación de docker. [En caso de trabajar con imagenes, de lo contrario iniciar JavEasReaderDbParameterizationApplication]
    * Cree un network para comunicación en local entre microservicios. `docker network create toures-balon-network`
* Instalación y datos de mongo.
    * Para este caso de muestra el proceso usando imagenes docker:
        * Ejecute en terminal `docker pull mongo` esto descarga la ultima versión.
        * Ejecute en terminal `docker run -d --name mongo1 -p 27017:27017 \
                                   -e MONGO_INITDB_ROOT_USERNAME=mongoadmin \
                                   -e MONGO_INITDB_ROOT_PASSWORD=secret \
                                   mongo`
        * Use un cliente de mongodb por ejemplo DataGrip y realice una nueva conexión, tenga en cuenta que las credenciales de acceso se enviaron como variables de entorno en 
          en el docker run de mongo. 
        * Ejecute el script de registro inicial de proveedores, [[AQUI](https://github.com/AESJaverianaPica2020/jav-eas-providers-data-model/blob/main/datos.txt)]
* Instalación y configuración de kafka.
    * Descargue kafka usando el comando `brew install kafka` en terminal linux.
    * Inicie el zookeeper usando el comando `brew services start zookeeper`
    * Inicie el zookeeper usando el comando `brew services start kafka`
    * Ingrese al directorio de instalación kafka a la caperta `bin`
    * Ejecute los comandos para crear topicos:
        * `sh kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic topic-info-reader`
        * `sh kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic topic-2-normalizer`
    
#### Instalación.

* Descargue el repositorio `git clone https://github.com/AESJaverianaPica2020/jav-eas-reader-db-parameterization.git`
* Ejecute `mvn clean install` para descargar dependencias y compilar el proyecto [Genera el .jar para poder hacer uso del dockerfile].
* Si usa docker, ejecute:
    * `docker build -t reader-params:0.1 .` Para generar la imagen.
    * `docker run --network toures-balon-network -ti --name reader-params_01 -p 9071:9071  \
                                     -e MONGO_HOST_TOURES=host.docker.internal \
                                     -e MONGO_PORT_TOURES=27017 \
                                     -e MONGO_DATABASE_TOURES=touresbalon \
                                     -e MONGO_USER_TOURES=mongoadmin \
                                     -e MONGO_PASSWORD_TOURES=secret \
                                     -e KAFKA_SERVER_TOURES=host.docker.internal:9092 \
                                     -e KAFKA_TOPIC_PRODUCER_TOURES=topic-2-normalizer \
                                     -e ENDPOINT_TRANSFORMER_TOURES=https://kafkatest-cobol.free.beeceptor.com \
                                     -e PATH_TRANSFORMER_TOURES=my/api/path/test \
                                     reader-params:0.1` las variables asignadas son a modo de ejemplo.
* Si no usa docker, simplemente `Run` sobre JavEasReaderDbParameterizationApplication.class

### Recurso:

Para poder realizar el consumo del recurso que premite la conexión con los proveedores por base de datos tenga en cuenta:

**PATH BASE:** /V1/Enterprise/providers
* V1 -> Version Uno.
* Enterprise -> Api de dominio empresarial
* providers -> Dominio del api

<table>
    <tr>
        <td>PATH</td>
        <td>DESCRIPCIÓN</td>
        <td>TIPO</td>
        <td>VERBO</td>
        <td>ESTRUCTURA DEL REQUEST</td>
        <td>HTTP CODE OK</td>
        <td>HTTP CODES FAILED</td>
    </tr>
    <tr>
        <td>/{name}</td>
        <td>Recibe el nombre del proveedor, para retornar la información de sus configuraciones</td>
        <td>SINCRONA</td>
        <td>GET</td>
        <td>Headers: <br> 
                        <strong>X-Type</strong> con el valor del tipo de proovedor</td>
        <td>200 - OK -</td>
        <td>
            500 - INTERNAL_SERVER_ERROR - Error interno el ejecución de proceso <br>
    </tr>
</table>
