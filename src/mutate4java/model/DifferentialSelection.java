package mutate4java.model;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import java.util.List;

public record DifferentialSelection(List<MutationSite> selected,
                                    boolean unchangedModule,
                                    DifferentialSurfaceArea surfaceArea) {
}

/* mutate4java-manifest
version=1
moduleHash=fbdc73c9ad4345729e04609cb90cfd65f9dbc7b13b09c347bd21413de7db32ae
scope.0.id=Y2xhc3M6RGlmZmVyZW50aWFsU2VsZWN0aW9uI0RpZmZlcmVudGlhbFNlbGVjdGlvbjoxOA
scope.0.kind=class
scope.0.startLine=18
scope.0.endLine=21
scope.0.semanticHash=ec0ab0f174ae14e99c5a1cd5b5e3899498c235e48bdaa264deb5fc1e5b07d730
scope.1.id=ZmllbGQ6RGlmZmVyZW50aWFsU2VsZWN0aW9uI3NlbGVjdGVkOjE4
scope.1.kind=field
scope.1.startLine=18
scope.1.endLine=18
scope.1.semanticHash=86a0598a1221f1369c897132ad8c64024634fb4a93e2e6a39c935a9aebe33fec
scope.2.id=ZmllbGQ6RGlmZmVyZW50aWFsU2VsZWN0aW9uI3N1cmZhY2VBcmVhOjIw
scope.2.kind=field
scope.2.startLine=20
scope.2.endLine=20
scope.2.semanticHash=8361c92dcd907c2354901495b4692cb90f8ba69ea4d954a202bd720a6d4372a0
scope.3.id=ZmllbGQ6RGlmZmVyZW50aWFsU2VsZWN0aW9uI3VuY2hhbmdlZE1vZHVsZToxOQ
scope.3.kind=field
scope.3.startLine=19
scope.3.endLine=19
scope.3.semanticHash=3339d5bf5751b9930aa106167a5c7b2151733a4bbafd8bc45708f732f35251d7
scope.4.id=bWV0aG9kOkRpZmZlcmVudGlhbFNlbGVjdGlvbiNjdG9yKDMpOjE4
scope.4.kind=method
scope.4.startLine=1
scope.4.endLine=21
scope.4.semanticHash=394c86a116f6899f8086bf542a7e42e12889397bb32495ae9098775f1d28190e
*/
