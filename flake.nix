{
  description = "A Maven plugin to run the Spoon analysis and transformation engine for Java";

  # Nixpkgs / NixOS version to use.
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs = { self, nixpkgs }:
    let
      # Helper function to generate an attrset '{ x86_64-linux = f "x86_64-linux"; ... }'.
      forAllSystems = nixpkgs.lib.genAttrs nixpkgs.lib.systems.flakeExposed;
      mkShell = system: { release ? false, javaVersion }:
        let
          pkgs = import nixpkgs {
            inherit system;
            overlays = [
              (final: prev:
                let
                  base = rec {
                    jdk = if javaVersion < 21 then prev."jdk${toString javaVersion}" else jdk21-ea;
                    maven = prev.maven.override { inherit jdk; };
                  };
                in
                base)
            ];
          };
          jdk21-ea = pkgs.stdenv.mkDerivation rec {
            name = "jdk21-oracle";
            version = "21+35";
            src = builtins.fetchTarball {
              url = "https://download.java.net/java/GA/jdk21/fd2272bbf8e04c3dbaee13770090416c/35/GPL/openjdk-21_linux-x64_bin.tar.gz";
              sha256 = "sha256:0g3vf0kcpciixfv1kvgbk685h6cfn1s0cx2di4rhl3r7xlal217w";
            };
            installPhase = ''
              cd ..
              mv $sourceRoot $out
            '';
          };
          semver = pkgs.buildGoModule rec {
            name = "semver";
            version = "2.1.0";

            vendorHash = "sha256-HKqZbgP7vqDJMaHUbSqfSOnBYwzOtIr9o2v/T9S+uNg=";
            subPackages = [ "cmd/semver" ];

            src = pkgs.fetchFromGitHub {
              owner = "ffurrer2";
              repo = "semver";
              rev = "v${version}";
              sha256 = "sha256-i/XPA2Hr2puJFKupIeBUE/yFPJxSeVsDWcz1OepxIcU=";
            };
          };
          jreleaser = pkgs.stdenv.mkDerivation rec {
            pname = "jreleaser-cli";
            version = "1.7.0";

            src = pkgs.fetchurl {
              url = "https://github.com/jreleaser/jreleaser/releases/download/v${version}/jreleaser-tool-provider-${version}.jar";
              sha256 = "sha256-gr1IWisuep00xyoZWKXtHymWkQjbDhlk6+UC16bKXu0=";
            };

            nativeBuildInputs = with pkgs; [ makeWrapper ];

            dontUnpack = true;

            installPhase = ''
              mkdir -p $out/share/java/ $out/bin/
              cp $src $out/share/java/${pname}.jar
              makeWrapper ${pkgs.jdk}/bin/java $out/bin/${pname} \
                --add-flags "-jar $out/share/java/${pname}.jar"
            '';
          };
        in
        pkgs.mkShell rec {
          test = pkgs.writeScriptBin "test" ''
            set -eu
            # this is to download the final test dependencies required to run the tests
            (cd src/test/projects/processors && mvn clean install)
            # Execute tests
            mvn test
          '';
          packages = with pkgs;
            [ jdk maven test ]
            ++ (if release then [ semver jreleaser ] else [ ]);
        };
    in
    {
      devShells =
        let
          # We have additional options (currently EA jdks) on 64 bit linux systems
          blessedSystem = "x86_64-linux";
          blessed = { jdk21-ea = mkShell blessedSystem { javaVersion = 21; }; };
          common = forAllSystems
            (system:
              rec {
                default = jdk20;
                jdk20 = mkShell system { javaVersion = 20; };
                jdk17 = mkShell system { javaVersion = 17; };
                jdk11 = mkShell system { javaVersion = 11; };
                jReleaser = mkShell system { release = true; javaVersion = 11; };
              });
        in
        common // { "${blessedSystem}" = common."${blessedSystem}" // blessed; };
    };
}
