import { ImageSize, ProcessedBundle } from '../model/ProcessedBundle.ts';
import { ProcessedBundlesProvider } from './bundle/ProcessedBundlesProvider.ts';
import { BundlesParser } from './bundle/Bundle.ts';
import { BundleProcessor } from './bundle/BundleProcessor.ts';
import fs from 'fs';
import { RatioParser } from '../RatioParser.ts';

function writeBundles(bundles: ProcessedBundle[], outputDir: string): void {
	bundles.forEach((bundle) => writeBundle(bundle, outputDir));
}
function writeBundle(bundle: ProcessedBundle, outputDir: string): void {
	const file = `${outputDir}/${bundle.name}.ts`;
	fs.writeFileSync(file, '//generated - do not edit\n');
	fs.appendFileSync(file, 'export default {\n');
	fs.appendFileSync(file, `\tname: '${bundle.name}',\n`);
	fs.appendFileSync(file, `\timageSizes: [\n`);
	writeSizes(bundle.imageSizes, file);
	fs.appendFileSync(file, `\t],\n`);
	fs.appendFileSync(file, `\tcustomRenditions: [\n`);
	writeSizes(bundle.customRenditions, file);
	fs.appendFileSync(file, `\t],\n`);
	fs.appendFileSync(file, `};\n`);
}
function writeSizes(sizes: ImageSize[], file: string): void {
	sizes.forEach((size) => writeSize(size, file));
}
function writeSize(size: ImageSize, file: string): void {
	fs.appendFileSync(file, '\t\t{\n');
	fs.appendFileSync(file, `\t\t\tid: '${size.id}',\n`);
	fs.appendFileSync(file, `\t\t\twidth: ${size.width},\n`);
	if (size.ratio) {
		fs.appendFileSync(file, `\t\t\tratio: '${size.ratio}',\n`);
	}
	fs.appendFileSync(file, '\t\t},\n');
}

try {
	const bundlesConfigPath = process.argv[2];
	const outputDir = process.argv[3];
	const bundlesProvider = new ProcessedBundlesProvider(new BundlesParser(), new BundleProcessor(new RatioParser()));
	const bundles = bundlesProvider.getAll(bundlesConfigPath);
	writeBundles(bundles, outputDir);
} catch (error) {
	console.error(error);
	process.exit(1);
}
