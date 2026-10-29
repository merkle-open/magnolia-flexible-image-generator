import { inject, injectable } from 'tsyringe';
import { FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN, type FlexibleImageConfigProviderI } from '../ConfigProvider.ts';

@injectable()
export class ImageDigest {
	constructor(@inject(FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN) private readonly flexibleImageConfigProvider: FlexibleImageConfigProviderI) {}

	public getHash(input: string): string {
		return this.hashCode(`${this.flexibleImageConfigProvider.get().saltProvider()}${input}`).toString();
	}
	//same as in java - h=31h+charAt(i)
	private hashCode(input: string): number {
		let hash = 0;
		for (let i = 0; i < input.length; i++) {
			hash = (Math.imul(31, hash) + input.charCodeAt(i)) | 0;
		}
		return hash;
	}
}
