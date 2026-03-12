package mutate4java.analysis;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.manifest.*;

import com.sun.source.tree.AssignmentTree;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public final class MutationCatalog {

    private final ManifestSupport manifestSupport = new ManifestSupport();
    private final JavaSourceCompiler compiler = new JavaSourceCompiler();
    public List<MutationSite> discover(List<Path> files) throws IOException {
        List<MutationSite> sites = new ArrayList<>();
        for (Path file : files) {
            try {
                sites.addAll(analyze(file).sites());
            } catch (IOException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new IOException("Failed analyzing mutations for " + file, ex);
            }
        }
        sites.sort(Comparator.comparing(MutationSite::file).thenComparingInt(MutationSite::start));
        return sites;
    }
    public SourceAnalysis analyze(Path file) throws Exception {
        String raw = Files.readString(file);
        String source = manifestSupport.stripManifest(raw);
        List<MutationSite> sites = astSites(file, source);
        List<MutationScope> scopes = astScopes(file, source);
        return new SourceAnalysis(source, sites, scopes, manifestSupport.hashScopes(scopes));
    }

    private List<MutationSite> astSites(Path file, String source) throws IOException {
        List<MutationSite> sites = new ArrayList<>();
        CompiledSource compiled = compiler.compile(file);
        for (var unit : compiled.units()) {
            new AstMutationScanner(file, source, unit, compiled.trees(), sites).scan(unit, null);
        }
        return sites;
    }

    private List<MutationScope> astScopes(Path file, String source) throws IOException {
        List<MutationSite> ignoredSites = new ArrayList<>();
        List<MutationScope> scopes = new ArrayList<>();
        CompiledSource compiled = compiler.compile(file);
        for (var unit : compiled.units()) {
            AstMutationScanner scanner = new AstMutationScanner(file, source, unit, compiled.trees(), ignoredSites);
            scanner.scan(unit, null);
            scopes.addAll(scanner.scopes());
        }
        return scopes.stream().sorted(Comparator.comparing(MutationScope::id)).toList();
    }
}
