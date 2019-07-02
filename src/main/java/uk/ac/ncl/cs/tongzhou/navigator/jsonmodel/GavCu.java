package uk.ac.ncl.cs.tongzhou.navigator.jsonmodel;

public class GavCu {
    public String group;
    public String artifact;
    public String version;
    public String cuName;

    public GavCu() {
    }

    public GavCu(String groupId, String artifactId, String version, String compilationUnit) {
        this.group = groupId;
        this.artifact = artifactId;
        this.version = version;
        this.cuName = compilationUnit;
    }

    public GavCu(String gavCuString){
        String[] pathTokens = gavCuString.split(":");

        this.group = pathTokens[0];
        this.artifact = pathTokens[1];
        this.version = pathTokens[2];
        this.cuName = pathTokens[3];
    }
}
