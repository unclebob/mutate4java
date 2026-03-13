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

import java.nio.file.Path;
import java.util.List;

public record MutantResultSummary(Path sourceFile,
                                  TestRun baseline,
                                  String extra,
                                  List<MutationSite> uncovered,
                                  List<MutationResult> results) {
}

/* mutate4java-manifest
version=1
moduleHash=08d2a164134dbae6bee2e8b46aef3656eda32642dc0a36af3c4982f97939eb00
scope.0.id=Y2xhc3M6TXV0YW50UmVzdWx0U3VtbWFyeSNNdXRhbnRSZXN1bHRTdW1tYXJ5OjE5
scope.0.kind=class
scope.0.startLine=19
scope.0.endLine=24
scope.0.semanticHash=d17a9a3fbc477fdafb635e45be3b316351aa2a2e8265dd3196a73258e69e7ebb
scope.1.id=ZmllbGQ6TXV0YW50UmVzdWx0U3VtbWFyeSNiYXNlbGluZToyMA
scope.1.kind=field
scope.1.startLine=20
scope.1.endLine=20
scope.1.semanticHash=fa42908ea8a39560f4d3e2a71f3b9616ac12a2a50160f0d9ffc623c132930a6e
scope.2.id=ZmllbGQ6TXV0YW50UmVzdWx0U3VtbWFyeSNleHRyYToyMQ
scope.2.kind=field
scope.2.startLine=21
scope.2.endLine=21
scope.2.semanticHash=ba7a562765edb36b91972be2a18e21f9289e62e3935dda80ce45f66a5b012b39
scope.3.id=ZmllbGQ6TXV0YW50UmVzdWx0U3VtbWFyeSNyZXN1bHRzOjIz
scope.3.kind=field
scope.3.startLine=23
scope.3.endLine=23
scope.3.semanticHash=123f64f2dacbb4e8050f958dd2a21a308254ff2e93816ad0afdb68d3bc759f44
scope.4.id=ZmllbGQ6TXV0YW50UmVzdWx0U3VtbWFyeSNzb3VyY2VGaWxlOjE5
scope.4.kind=field
scope.4.startLine=19
scope.4.endLine=19
scope.4.semanticHash=61d34f23b74db96ae1f61e0adfe26948af29a75d59162ddf69fbf897c404026e
scope.5.id=ZmllbGQ6TXV0YW50UmVzdWx0U3VtbWFyeSN1bmNvdmVyZWQ6MjI
scope.5.kind=field
scope.5.startLine=22
scope.5.endLine=22
scope.5.semanticHash=7dd9888fa1be867590905516bd26210d3cc14c762db8e7367dadd2c067373983
scope.6.id=bWV0aG9kOk11dGFudFJlc3VsdFN1bW1hcnkjY3Rvcig1KToxOQ
scope.6.kind=method
scope.6.startLine=1
scope.6.endLine=24
scope.6.semanticHash=d2467dba513bb7ca96f609d7aeed3f167baeed7665c4f81eafbda90e9f5367ff
*/
