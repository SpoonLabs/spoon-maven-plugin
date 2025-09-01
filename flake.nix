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
                    jdk = prev."jdk${toString javaVersion}";
                    maven = prev.maven.override { jdk_headless = jdk; };
                  };
                in
                base)
            ];
          };
          semver = pkgs.buildGoModule rec {
            name = "semver";
            version = "2.11.0";

            vendorHash = "sha256-7xs+6gqy4WeP1L2VthZJ3IOYRKMWbpKdAQn6lu4Z5g0=";
            subPackages = [ "cmd/semver" ];

            src = pkgs.fetchFromGitHub {
              owner = "ffurrer2";
              repo = "semver";
              rev = "v${version}";
              sha256 = "sha256-BAONscuPUqfzEwDuyIqZroArEMnE6HmCfT9NTZ1LuXo=";
            };
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
            ++ (if release then [ semver pkgs.jreleaser-cli ] else [ ]);
        };
    in
    {
      devShells = forAllSystems (system:
        rec {
          default = jdk21;
          jdk24 = mkShell system { javaVersion = 24; };
          jdk21 = mkShell system { javaVersion = 21; };
          jdk17 = mkShell system { javaVersion = 17; };
          jdk11 = mkShell system { javaVersion = 11; };
          jReleaser = mkShell system { release = true; javaVersion = 21; };
        }
      );
    };
}
