import { inject, injectable } from 'tsyringe';
import { FlexibleParameter } from '../model/FlexibleParameter.ts';
import { FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN, type FlexibleImageConfigProviderI } from '../ConfigProvider.ts';

@injectable()
export class FlexibleImageUriFactory {
	private magnoliaFlexibleImageBaseUrl: string;

	constructor(@inject(FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN) flexibleImageConfigProvider: FlexibleImageConfigProviderI) {
		this.magnoliaFlexibleImageBaseUrl = flexibleImageConfigProvider.get().magnoliaApisProvider.flexibleImagingBase();
	}

	/*
	 * /<context>/.imaging/flex/assetItemKey/param1Key/param1Value/.../fileName
	 * e.g. /author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/height/316/width/560/version/1724145931/dummy1-1600x900.jpg
	 */
	public create(data: FlexibleParameter): string {
		const parameters = data.toMap();
		return encodeURI(
			[
				this.magnoliaFlexibleImageBaseUrl,
				data.id,
				...Object.keys(parameters)
					.sort()
					.filter((key) => parameters[key])
					.flatMap((key) => [key, parameters[key]]),
				data.name,
			].join('/'),
		);
	}
}
