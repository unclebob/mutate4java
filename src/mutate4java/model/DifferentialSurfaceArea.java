package mutate4java.model;

public record DifferentialSurfaceArea(boolean reportable,
                                      int unregisteredMutations,
                                      int manifestViolations) {

    public static DifferentialSurfaceArea notReported() {
        return new DifferentialSurfaceArea(false, 0, 0);
    }
}

/* mutate4java-manifest
version=1
moduleHash=99300759e014af3c19ee578c2dcd9f465025a7b5088ffab3a734fe6500aab762
scope.0.id=Y2xhc3M6RGlmZmVyZW50aWFsU3VyZmFjZUFyZWEjRGlmZmVyZW50aWFsU3VyZmFjZUFyZWE6Mw
scope.0.kind=class
scope.0.startLine=3
scope.0.endLine=10
scope.0.semanticHash=adfed8fff7d638c72da91f1d10fe1bbc2799714d4e2467bd6da0b2fdf16312b5
scope.1.id=ZmllbGQ6RGlmZmVyZW50aWFsU3VyZmFjZUFyZWEjbWFuaWZlc3RWaW9sYXRpb25zOjU
scope.1.kind=field
scope.1.startLine=5
scope.1.endLine=5
scope.1.semanticHash=bbf396bd12c94c4cb707ae29f369a07eadb022a5a266c64c9d53397df07fb2ef
scope.2.id=ZmllbGQ6RGlmZmVyZW50aWFsU3VyZmFjZUFyZWEjcmVwb3J0YWJsZToz
scope.2.kind=field
scope.2.startLine=3
scope.2.endLine=3
scope.2.semanticHash=d3024a447e57e0bee65992d9453d363abb950b5ae4c2ccaa3da5169014b344da
scope.3.id=ZmllbGQ6RGlmZmVyZW50aWFsU3VyZmFjZUFyZWEjdW5yZWdpc3RlcmVkTXV0YXRpb25zOjQ
scope.3.kind=field
scope.3.startLine=4
scope.3.endLine=4
scope.3.semanticHash=603ca266f64980d23aba70b027042e79aa25dd1dcbd62b625a0555b25a914dc4
scope.4.id=bWV0aG9kOkRpZmZlcmVudGlhbFN1cmZhY2VBcmVhI2N0b3IoMyk6Mw
scope.4.kind=method
scope.4.startLine=1
scope.4.endLine=10
scope.4.semanticHash=9d8c95219db282846e3a3fea54b6cb9cfdb7bb38af4409ed14e88e7e9f501fc7
scope.5.id=bWV0aG9kOkRpZmZlcmVudGlhbFN1cmZhY2VBcmVhI25vdFJlcG9ydGVkKDApOjc
scope.5.kind=method
scope.5.startLine=7
scope.5.endLine=9
scope.5.semanticHash=35ae4b00664f6927209734e1f94269a0dbfef276cf35ee2e0f3bc6efbba0f2b6
*/
