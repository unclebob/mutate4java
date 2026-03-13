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
                                    boolean manifestExists,
                                    boolean moduleHashChanged,
                                    int totalMutationSites,
                                    int changedMutationSites,
                                    int differentialSurfaceArea,
                                    int manifestViolatingSurfaceArea) {
}

/* mutate4java-manifest
version=1
moduleHash=7d503e902024985c6964caf1be803b4d79970a9a7e79cbeb9b846b1615ab5a34
scope.0.id=Y2xhc3M6RGlmZmVyZW50aWFsU2VsZWN0aW9uI0RpZmZlcmVudGlhbFNlbGVjdGlvbjoxOA
scope.0.kind=class
scope.0.startLine=18
scope.0.endLine=26
scope.0.semanticHash=fea28bc5711454f39e43c8e0e31f758e50d1c42a670cfbcb8984f0cb80c2461b
scope.1.id=ZmllbGQ6RGlmZmVyZW50aWFsU2VsZWN0aW9uI2NoYW5nZWRNdXRhdGlvblNpdGVzOjIz
scope.1.kind=field
scope.1.startLine=23
scope.1.endLine=23
scope.1.semanticHash=78a24837aa461539b024a8ddfef1c879a63f92781df543462b915130c71ea974
scope.2.id=ZmllbGQ6RGlmZmVyZW50aWFsU2VsZWN0aW9uI2RpZmZlcmVudGlhbFN1cmZhY2VBcmVhOjI0
scope.2.kind=field
scope.2.startLine=24
scope.2.endLine=24
scope.2.semanticHash=d0f79f59cba6c96f7a0156522686ee6cce016898e84a6922544af9e887a11bc9
scope.3.id=ZmllbGQ6RGlmZmVyZW50aWFsU2VsZWN0aW9uI21hbmlmZXN0RXhpc3RzOjIw
scope.3.kind=field
scope.3.startLine=20
scope.3.endLine=20
scope.3.semanticHash=be437a91a2d4a1a07df0c58596a6f80712b1149d1162c2d1bce00eacc9509839
scope.4.id=ZmllbGQ6RGlmZmVyZW50aWFsU2VsZWN0aW9uI21hbmlmZXN0VmlvbGF0aW5nU3VyZmFjZUFyZWE6MjU
scope.4.kind=field
scope.4.startLine=25
scope.4.endLine=25
scope.4.semanticHash=1f3c5a64ae7d21767325395ce7e23adeed8f46a288e858a08888a453cf483496
scope.5.id=ZmllbGQ6RGlmZmVyZW50aWFsU2VsZWN0aW9uI21vZHVsZUhhc2hDaGFuZ2VkOjIx
scope.5.kind=field
scope.5.startLine=21
scope.5.endLine=21
scope.5.semanticHash=a078283932e66f6b232fade4da300099bb577c081106ef1487fbe0eb679508ba
scope.6.id=ZmllbGQ6RGlmZmVyZW50aWFsU2VsZWN0aW9uI3NlbGVjdGVkOjE4
scope.6.kind=field
scope.6.startLine=18
scope.6.endLine=18
scope.6.semanticHash=86a0598a1221f1369c897132ad8c64024634fb4a93e2e6a39c935a9aebe33fec
scope.7.id=ZmllbGQ6RGlmZmVyZW50aWFsU2VsZWN0aW9uI3RvdGFsTXV0YXRpb25TaXRlczoyMg
scope.7.kind=field
scope.7.startLine=22
scope.7.endLine=22
scope.7.semanticHash=4c57a4d726610f2e29faa44d978dd99b4e9c9fda94647bd9da367286bd64ed05
scope.8.id=ZmllbGQ6RGlmZmVyZW50aWFsU2VsZWN0aW9uI3VuY2hhbmdlZE1vZHVsZToxOQ
scope.8.kind=field
scope.8.startLine=19
scope.8.endLine=19
scope.8.semanticHash=3339d5bf5751b9930aa106167a5c7b2151733a4bbafd8bc45708f732f35251d7
scope.9.id=bWV0aG9kOkRpZmZlcmVudGlhbFNlbGVjdGlvbiNjdG9yKDgpOjE4
scope.9.kind=method
scope.9.startLine=1
scope.9.endLine=26
scope.9.semanticHash=1aeb7c22185087bf8f4b308fc0ea2b933aaec0b02b2eaeaf87ac2ed0e6d6f851
*/
