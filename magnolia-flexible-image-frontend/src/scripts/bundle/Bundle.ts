import { globSync, readFileSync } from 'fs';

export interface Bundle {
	bundle: string;
	ratio?: string;
	imageSizes: ImageSize[];
	customRenditions: ImageSize[];
}

export interface ImageSize {
	id: string;
	width?: number;
	ratio: string;
}

interface BundlesConfig {
	scanPaths: string[];
	bundlesDirName: string;
}

export class BundlesParser {
	public parse(bundlesConfigFilePath: string): Bundle[] {
		const bundlesConfig = this.getBundlesConfig(bundlesConfigFilePath);
		return this.create(bundlesConfig);
	}

	private getBundlesConfig(bundlesConfigFilePath: string): BundlesConfig {
		const config: BundlesConfig = JSON.parse(readFileSync(bundlesConfigFilePath, 'utf8'));
		config.scanPaths = config.scanPaths.map((path) => `${process.cwd()}/${path}`);
		return config;
	}

	private create(bundlesConfig: BundlesConfig): Bundle[] {
		return bundlesConfig.scanPaths.flatMap((path) => this.createBundles(bundlesConfig.bundlesDirName, path));
	}

	private createBundles(bundlesDirName: string, path: string): Bundle[] {
		return globSync(`${path}/**${bundlesDirName}/*.json`)
			.map((file) => readFileSync(file, 'utf8'))
			.map((json) => JSON.parse(json))
			.flatMap((bundles: Bundle[][]): Bundle[] => bundles.flatMap((bundle) => bundle));
	}
}
