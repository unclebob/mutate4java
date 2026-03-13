package mutate4java.selection;

import java.util.LinkedHashSet;
import java.util.Set;

record ChangedScopes(boolean manifestPresent,
                     boolean moduleHashChanged,
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
moduleHash=81d39642dacf1bb264bdb2be558b63f2aef9b6246a7bc4b1b256b3ed35b5b2c7
scope.0.id=Y2xhc3M6Q2hhbmdlZFNjb3BlcyNDaGFuZ2VkU2NvcGVzOjY
scope.0.kind=class
scope.0.startLine=6
scope.0.endLine=16
scope.0.semanticHash=c51d9768d184615c423a17b4c3d3ff1f30d525f19d5a08cdf9c83b9c19d30eea
scope.1.id=ZmllbGQ6Q2hhbmdlZFNjb3BlcyNtYW5pZmVzdFByZXNlbnQ6Ng
scope.1.kind=field
scope.1.startLine=6
scope.1.endLine=6
scope.1.semanticHash=575701f4af7387b144c9a3e7cabdd7c1b78a4f5ff1291ac410122128de88669b
scope.2.id=ZmllbGQ6Q2hhbmdlZFNjb3BlcyNtYW5pZmVzdFZpb2xhdGlvblNjb3BlSWRzOjk
scope.2.kind=field
scope.2.startLine=9
scope.2.endLine=9
scope.2.semanticHash=bf4cdf1bb63bb91268d79604f28a015cac50f85c78a0712ed51f63202c6cfbeb
scope.3.id=ZmllbGQ6Q2hhbmdlZFNjb3BlcyNtb2R1bGVIYXNoQ2hhbmdlZDo3
scope.3.kind=field
scope.3.startLine=7
scope.3.endLine=7
scope.3.semanticHash=a078283932e66f6b232fade4da300099bb577c081106ef1487fbe0eb679508ba
scope.4.id=ZmllbGQ6Q2hhbmdlZFNjb3BlcyN1bnJlZ2lzdGVyZWRTY29wZUlkczo4
scope.4.kind=field
scope.4.startLine=8
scope.4.endLine=8
scope.4.semanticHash=de003db5b078a0eb3c681b292fff6fecb3d8f9cb174067b43e491ebcb510129d
scope.5.id=bWV0aG9kOkNoYW5nZWRTY29wZXMjYWxsU2NvcGVJZHMoMCk6MTE
scope.5.kind=method
scope.5.startLine=11
scope.5.endLine=15
scope.5.semanticHash=fe4123a8af853e45ae549316cac524076144608e56f97aa7b7f755333ca93a32
scope.6.id=bWV0aG9kOkNoYW5nZWRTY29wZXMjY3Rvcig0KTo2
scope.6.kind=method
scope.6.startLine=1
scope.6.endLine=16
scope.6.semanticHash=24992b192dca48be26a98c35774cf9aaab69ce6e520ad351db50c94314b7d2cb
*/
