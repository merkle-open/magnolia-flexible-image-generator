export const TOKEN_PREFIX = 'com.merkle.oss.magnolia.imaging.flexible.';

export function token(suffix: string): symbol {
	return Symbol(TOKEN_PREFIX + suffix);
}
