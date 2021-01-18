### About

`nuxeo-extended-session` is a Nuxeo Plugin that allows to shared the authentication session between Nuxeo nodes.

### Why

Most web authentication systems require the usage of an http session at the server level.

As a result, by default, most of the Nuxeo Authentication plugins will force the creation of a Session at the Tomcat level and the creation of a `JSESSIONID`.
In the context of a Nuxeo Cluster, it implies that a given client will always go to the same server so that the http session can be recovered: failing to do so, the client is likely to be consiered unauthenticated.

Session affinity allows to solve this issue and also provides some efficiency gains:

 - no need to retrieve user information from a Database for every single request
 - more efficiency for the caching systems

However, in a production environments, especially when run in a container environment, server can come and go:

 - rolling upgrade
 - blue/green deployment
 - scale out / scale down

Ideally, we need these processes to be transparent from the application point of view.

This basically means:

 - keep session affinity by default (for efficiency)
 - provide a way to recover when affinity can not be maintained (for resiliency)

### How this work

#### Extented Session Cookie

An extended Session Cookie is associated (by a servlet filter) to each new "client".

This extended Session Cookie holds an `Extended Session Key`.

#### Extended Session Listener

A listener will intercept Authentication events:

 - `loginSuccess` : store user identification in the KV Store using the Extended Session Key
 - `logout` : delete the entry in the KVS corresponding tp the Extended Session key

#### Extended Session Authenticator

The `ExtendedSessionAuth` is a `NuxeoAuthenticationPlugin` that should be part of the Authentication Chain.
In case no authentication info can be retrieved from Http Session, it will try to retrieve the session from the KVS.

#### KV Store

The Extended Session storage is handled by the `KeyValueStore`.
The actual implementation will depend on the deployment configuration (Mem Redis, SQL, MongoDB).

The Extended Session informatiuon is stored with a TTL so that no explicit garbage collecting is needed.

### Deployment

Once the plugin is deployed you need to configure the Authentication chain to include `NUXEO_EXTENDED_SESSION`

Sample XML configuration:
   
    ...   
    <require>org.nuxeo.ecm.platform.ui.web.auth.defaultConfig</require>
     
    <extension target="org.nuxeo.ecm.platform.ui.web.auth.service.PluggableAuthenticationService" point="chain">
     <authenticationChain>
      <plugins>
        <plugin>NUXEO_EXTENDED_SESSION</plugin>
        <plugin>BASIC_AUTH</plugin>
        <plugin>TOKEN_AUTH</plugin>
        <plugin>FORM_AUTH</plugin>
      </plugins>
     </authenticationChain>
    </extension>
    ...
    
