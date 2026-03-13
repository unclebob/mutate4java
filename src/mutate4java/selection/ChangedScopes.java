package mutate4java.selection;

import java.util.LinkedHashSet;
import java.util.Set;

record ChangedScopes(boolean manifestPresent,
                     Set<String> unregisteredScopeIds,
                     Set<String> manifestViolationScopeIds) {

    Set<String> allScopeIds() {
        Set<String> ids = new LinkedHashSet<>(unregisteredScopeIds);
        ids.addAll(manifestViolationScopeIds);
        return Set.copyOf(ids);
    }
}

/* mutate4java-manifest
version=1
moduleHash=47ce984506b5fdab89c1f2da0a767c3dd0ca5e7229082bffe6318577d1e4e00a
scope.0.id=Y2xhc3M6Q2hhbmdlZFNjb3BlcyNDaGFuZ2VkU2NvcGVzOjY
scope.0.kind=class
scope.0.startLine=6
scope.0.endLine=15
scope.0.semanticHash=d6c5c69af30ef5d5b70c10f9fec053862357fb2a093ebd2d54304db443856adb
scope.1.id=ZmllbGQ6Q2hhbmdlZFNjb3BlcyNtYW5pZmVzdFByZXNlbnQ6Ng
scope.1.kind=field
scope.1.startLine=6
scope.1.endLine=6
scope.1.semanticHash=575701f4af7387b144c9a3e7cabdd7c1b78a4f5ff1291ac410122128de88669b
scope.2.id=ZmllbGQ6Q2hhbmdlZFNjb3BlcyNtYW5pZmVzdFZpb2xhdGlvblNjb3BlSWRzOjg
scope.2.kind=field
scope.2.startLine=8
scope.2.endLine=8
scope.2.semanticHash=bf4cdf1bb63bb91268d79604f28a015cac50f85c78a0712ed51f63202c6cfbeb
scope.3.id=ZmllbGQ6Q2hhbmdlZFNjb3BlcyN1bnJlZ2lzdGVyZWRTY29wZUlkczo3
scope.3.kind=field
scope.3.startLine=7
scope.3.endLine=7
scope.3.semanticHash=de003db5b078a0eb3c681b292fff6fecb3d8f9cb174067b43e491ebcb510129d
scope.4.id=bWV0aG9kOkNoYW5nZWRTY29wZXMjYWxsU2NvcGVJZHMoMCk6MTA
scope.4.kind=method
scope.4.startLine=10
scope.4.endLine=14
scope.4.semanticHash=fe4123a8af853e45ae549316cac524076144608e56f97aa7b7f755333ca93a32
scope.5.id=bWV0aG9kOkNoYW5nZWRTY29wZXMjY3RvcigzKTo2
scope.5.kind=method
scope.5.startLine=1
scope.5.endLine=15
scope.5.semanticHash=f3c321571309e6c80612303cd303bd4d5fadd6b054b5691ca94c6aa5ac2ff4a3
*/
