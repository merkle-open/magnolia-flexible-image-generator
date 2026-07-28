import { ProcessedBundle } from '../../model/ProcessedBundle.ts';
import { BundlesParser } from './Bundle.ts';
import { BundleProcessor } from './BundleProcessor.ts';

export class ProcessedBundlesProvider {
	constructor(
		private readonly bundlesParser: BundlesParser,
		private readonly bundleProcessor: BundleProcessor,
	) {}

	public getAll(bundlesConfigPath: string): ProcessedBundle[] {
		return this.bundlesParser.parse(bundlesConfigPath).map((bundle) => this.bundleProcessor.process(bundle));
	}
}
